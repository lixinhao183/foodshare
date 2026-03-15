package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.mapper.CommentMapper;
import xinhao.foodshare.mapper.FavouriteMapper;
import xinhao.foodshare.mapper.LikesMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.PostStateMapper;
import xinhao.foodshare.mapper.TagMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.ViewHistoryMapper;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Favourite;
import xinhao.foodshare.pojo.entity.Likes;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.PostState;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.ViewHistory;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.service.PostService;
import xinhao.foodshare.utils.PostUtils;
import xinhao.foodshare.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * 帖子服务实现类
 * 处理帖子相关的业务逻辑，如列表查询、发布等
 */

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private PostStateMapper postStateMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ViewHistoryMapper viewHistoryMapper;

    @Autowired
    private PostUtils postUtils;

    @Autowired
    private TagMapper tagMapper;

    /**
     * 分页查询帖子列表
     * 用于前端首页或发现页的无限加载流
     *
     * @param page     当前页码
     * @param pageSize 每页显示数量
     * @param sort     排序方式，"new" 按发布时间倒序，"hot" 按点赞数倒序
     * @param local    地点
     * @param keyword  搜索关键词
     * @param tags     标签列表
     * @return 封装好的分页结果，包含帖子详情、作者信息及当前用户的点赞状态
     */
    @Override
    public PageResult<PostVO> pageQuery(Integer page, Integer pageSize, String sort, String local, String price,
            String keyword, List<String> tags) {
        // 构造缓存key (包含搜索关键词和标签)
        String cacheKey = "post_page:" + sort + ":" + page + "_" + pageSize + "_" +
                (local != null ? local : "all") + ":" +
                (price != null ? price : "all") + ":" +
                (keyword != null ? keyword : "all") + ":" +
                (tags != null && !tags.isEmpty() ? String.join(",", tags) : "all");

        // 1. 尝试从缓存获取 (只有查询热门帖子时才使用缓存)
        PageResult<PostVO> result = null;
        if ("hot".equals(sort)) {
            result = redisCache.getCacheObject(cacheKey);
            if (result != null) {
                return result;
            }
        }

        // 2. 基础分页查询
        Page<Post> postPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        // 过滤条件：只能看到审核通过(status=2) 且 未被删除(isDeleted=0) 的帖子
        queryWrapper.eq(Post::getStatus, 2)
                .eq(Post::getIsDeleted, 0);

        // 如果有位置参数，添加位置过滤条件
        if (local != null && !local.isEmpty()) {
            queryWrapper.eq(Post::getLocal, local);
        }

        // 如果有价格参数，添加价格过滤条件
        if (price != null && !price.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .ge(Post::getPrice, price.split("-")[0])
                    .le(Post::getPrice, price.split("-")[1]));
        }

        // 如果有标签参数，添加标签过滤条件 (OR 关系，只要包含其中一个标签即可)
        if (tags != null && !tags.isEmpty()) {
            queryWrapper.and(wrapper -> {
                for (String tag : tags) {
                    wrapper.or().like(Post::getTag, tag);
                }
            });
        }

        // 如果有搜索关键词，添加模糊查询条件 (标题或内容包含关键词)
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Post::getTitle, keyword));
        }

        // 根据排序参数添加排序条件
        if ("new".equals(sort)) {
            queryWrapper.orderByDesc(Post::getCreateTime);
        } else if ("hot".equals(sort)) {
            queryWrapper.orderByDesc(Post::getLikeCount);
        } else {
            // 默认排序：按发布时间倒序
            queryWrapper.orderByDesc(Post::getCreateTime);
        }

        // 执行分页查询，结果会自动填充到 postPage 中
        this.page(postPage, queryWrapper);

        List<Post> records = postPage.getRecords();
        if (records.isEmpty()) {
            // 如果没有数据，直接返回空列表，避免后续空指针或无意义查询
            return new PageResult<>(new ArrayList<>(), 0);
        }

        // 3. 调用工具类填充关联信息
        List<PostVO> vos = postUtils.fillPostDetails(records);

        result = new PageResult<>(vos, postPage.getTotal());

        // 存入缓存 (只有查询热门帖子时才缓存)
        if ("hot".equals(sort)) {
            redisCache.setCacheObject(cacheKey, result, 5, TimeUnit.MINUTES);
        }

        return result;
    }

/**
     * 分页查询标签
     * @param page 页码
     * @param pageSize 每页数量
     * @return 标签列表
     */
    @Override
    public PageResult<Tag> getTags(Integer page, Integer pageSize) {
        Page<Tag> tagPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(Tag::getUseCount);

        tagMapper.selectPage(tagPage, queryWrapper);
        
        return new PageResult<>(tagPage.getRecords(), tagPage.getTotal());
    }

    /**
     * 分页查询评论
     * 
     * @param postId   帖子ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @Override
    public PageResult<Comment> getComments(Long postId, Integer page, Integer pageSize) {
        Page<Comment> commentPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Comment::getPostId, postId)
                .orderByDesc(Comment::getCreateTime);

        commentMapper.selectPage(commentPage, queryWrapper);

        List<Comment> comments = commentPage.getRecords();
        if (comments.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), commentPage.getTotal());
        }

        // 填充评论者信息
        Set<Long> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getUserId, userIds))
                .stream().collect(Collectors.toMap(User::getUserId, u -> u));

        comments.forEach(c -> {
            User user = userMap.get(c.getUserId());
            if (user != null) {
                c.setUsername(user.getUsername());
                c.setAvatar(user.getImage());
            }
        });

        return new PageResult<>(comments, commentPage.getTotal());
    }

    /**
     * 查询帖子详情
     * 
     * @param postId 帖子ID
     * @return 帖子详情数据
     */
    @Override
    public PostVO detail(Long postId) {

        // 将帖子游览次数加1 (PostState表)
        LambdaUpdateWrapper<PostState> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("view_count = view_count + 1")
                .eq(PostState::getPostId, postId);
        postStateMapper.update(null, updateWrapper);

        // 将帖子游览次数加1 (Post表)
        LambdaUpdateWrapper<Post> postUpdateWrapper = new LambdaUpdateWrapper<>();
        postUpdateWrapper.setSql("view_count = view_count + 1")
                .eq(Post::getPostId, postId);
        postMapper.update(null, postUpdateWrapper);

        Post post = this.getById(postId);
        if (post == null || post.getIsDeleted() == 1 || post.getStatus() != 2) {
            throw new RuntimeException("帖子不存在或已被删除");
        }

        // 转换为VO
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo, "images", "tags");

        // 转换图片和标签格式
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> images = objectMapper.readValue(post.getImages(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                        });
                vo.setImages(images);
            } catch (JsonProcessingException e) {
                // 如果解析失败，可能是旧数据或格式错误，视为空列表或不做处理
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }

        if (post.getTag() != null && !post.getTag().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> tags = objectMapper.readValue(post.getTag(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                        });
                vo.setTags(tags);
            } catch (JsonProcessingException e) {
                vo.setTags(new ArrayList<>());
            }
        } else {
            vo.setTags(new ArrayList<>());
        }

        // 填充作者信息
        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getImage());
        }

        // 填充统计数据
        PostState state = postStateMapper.selectById(postId);
        if (state != null) {
            vo.setLikeCount(state.getLikeCount());
            vo.setCommentCount(state.getCommentCount());
            vo.setViewCount(state.getViewCount());
            vo.setFavouriteCount(state.getFavouriteCount());
        }

        // 查询评论列表
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getParentId, 0) // 只查询一级评论？原逻辑是这样的
                .orderByAsc(Comment::getCreateTime);
        List<Comment> commentsList = commentMapper.selectList(commentWrapper);

        // 填充评论用户信息和点赞数
        for (Comment comment : commentsList) {
            User commentUser = userMapper.selectById(comment.getUserId());
            if (commentUser != null) {
                comment.setUsername(commentUser.getUsername());
                comment.setAvatar(commentUser.getImage());
            }

            // 查询评论点赞数
            LambdaQueryWrapper<Likes> likeCountWrapper = new LambdaQueryWrapper<>();
            likeCountWrapper.eq(Likes::getTargetType, 1)
                    .eq(Likes::getTargetId, comment.getCommentId());
            comment.setLikeCount(likesMapper.selectCount(likeCountWrapper));

            // 初始化点赞状态
            comment.setIsLiked(0);
        }
        vo.setComments(commentsList);

        // 3. 填充当前用户的交互状态 (点赞、收藏)
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null) {
            // 查询帖子是否点赞
            LambdaQueryWrapper<Likes> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(Likes::getUserId, currentUserId)
                    .eq(Likes::getTargetType, 0)
                    .eq(Likes::getTargetId, postId);
            vo.setIsLiked(likesMapper.selectCount(likeWrapper) > 0 ? 1 : 0);

            // 查询帖子是否收藏
            LambdaQueryWrapper<Favourite> favouriteWrapper = new LambdaQueryWrapper<>();
            favouriteWrapper.eq(Favourite::getUserId, currentUserId)
                    .eq(Favourite::getPostId, postId);
            vo.setIsFavourite(favouriteMapper.selectCount(favouriteWrapper) > 0 ? 1 : 0);

            // 查询帖子的评论是否点赞
            if (vo.getComments() != null) {
                for (Comment comment : vo.getComments()) {
                    LambdaQueryWrapper<Likes> isLikedWrapper = new LambdaQueryWrapper<>();
                    isLikedWrapper.eq(Likes::getUserId, currentUserId)
                            .eq(Likes::getTargetType, 1)
                            .eq(Likes::getTargetId, comment.getCommentId());
                    comment.setIsLiked(likesMapper.selectCount(isLikedWrapper) > 0 ? 1 : 0);
                }
            }

            // 保存或更新游览记录
            LambdaQueryWrapper<ViewHistory> viewHistoryWrapper = new LambdaQueryWrapper<>();
            viewHistoryWrapper.eq(ViewHistory::getUserId, currentUserId)
                    .eq(ViewHistory::getPostId, postId);

            ViewHistory existingHistory = viewHistoryMapper.selectOne(viewHistoryWrapper);

            if (existingHistory != null) {
                // 如果记录已存在，更新游览时间
                existingHistory.setViewTime(LocalDateTime.now());
                viewHistoryMapper.updateById(existingHistory);
            } else {
                // 如果记录不存在，插入新记录
                ViewHistory viewHistory = new ViewHistory();
                viewHistory.setUserId(currentUserId);
                viewHistory.setPostId(postId);
                viewHistory.setViewTime(LocalDateTime.now());
                viewHistoryMapper.insert(viewHistory);
            }

        } else {
            // 游客状态
            vo.setIsLiked(0);
            vo.setIsFavourite(0);
            if (vo.getComments() != null) {
                for (Comment comment : vo.getComments()) {
                    comment.setIsLiked(0);
                }
            }
        }

        return vo;
    }
}
