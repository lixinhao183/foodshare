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
import xinhao.foodshare.mapper.FollowsMapper;
import xinhao.foodshare.mapper.LikesMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.PostStateMapper;
import xinhao.foodshare.mapper.TagMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.ViewHistoryMapper;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Favourite;
import xinhao.foodshare.pojo.entity.Follows;
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
    private FollowsMapper followsMapper;

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

    @Autowired
    private xinhao.foodshare.mapper.LocalMapper localMapper;

    /**
     * 分页查询帖子列表
     * 用于前端首页或发现页
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
        // 过滤条件：只能看到审核通过(status=2)
        queryWrapper.eq(Post::getStatus, 2);

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
     * @param tagName 标签名称（可选，模糊查询）
     * @return 标签列表
     */
    @Override
    public PageResult<Tag> getTags(Integer page, Integer pageSize, String tagName) {
        Page<Tag> tagPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果有标签名称，添加模糊查询条件
        if (tagName != null && !tagName.isEmpty()) {
            queryWrapper.like(Tag::getTagName, tagName);
        }

        // 按使用次数倒序
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

        // 1. 批量填充评论者信息
        Set<Long> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getUserId, userIds))
                .stream().collect(Collectors.toMap(User::getUserId, u -> u));

        // 2. 获取当前用户ID用于判断点赞状态
        Long currentUserId = SecurityUtils.getUserId();
        Set<Long> likedCommentIds = new java.util.HashSet<>();
        if (currentUserId != null) {
            Set<Long> commentIds = comments.stream().map(Comment::getCommentId).collect(Collectors.toSet());
            likedCommentIds = likesMapper.selectList(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getUserId, currentUserId)
                    .eq(Likes::getTargetType, 1)
                    .in(Likes::getTargetId, commentIds))
                    .stream().map(Likes::getTargetId).collect(Collectors.toSet());
        }

        // 3. 填充详细信息
        for (Comment c : comments) {
            // 填充用户信息
            User user = userMap.get(c.getUserId());
            if (user != null) {
                c.setUsername(user.getUsername());
                c.setAvatar(user.getImage());
            }

            // 查询评论点赞数 (此处沿用原逻辑，若后续需优化建议使用聚合查询)
            LambdaQueryWrapper<Likes> likeCountWrapper = new LambdaQueryWrapper<>();
            likeCountWrapper.eq(Likes::getTargetType, 1)
                    .eq(Likes::getTargetId, c.getCommentId());
            c.setLikeCount(likesMapper.selectCount(likeCountWrapper));

            // 填充点赞状态
            c.setIsLiked(likedCommentIds.contains(c.getCommentId()) ? 1 : 0);
        }

        return new PageResult<>(comments, commentPage.getTotal());
    }

    @Override
    public PostVO detail(Long postId) {

        // 1. 异步更新或优化全局游览次数增加逻辑 (PostState表 & Post表)
        // 注意：如果 PostState 记录不存在，UpdateWrapper 将不会执行任何操作。
        // 这里可以考虑增加不存在则插入的逻辑，或者在发布帖子时初始化 PostState。
        postStateMapper.update(null, new LambdaUpdateWrapper<PostState>()
                .setSql("view_count = view_count + 1")
                .eq(PostState::getPostId, postId));

        postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .setSql("view_count = view_count + 1")
                .eq(Post::getPostId, postId));

        Post post = this.getById(postId);
        if (post == null) {
            return null; // 或者抛出异常
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

        // 填充位置名称
        if (post.getLocal() != null) {
            xinhao.foodshare.pojo.entity.Local localEntity = localMapper.selectById(post.getLocal());
            if (localEntity != null) {
                vo.setLocalName(localEntity.getLocalName());
            }
        }

        // 填充统计数据
        PostState state = postStateMapper.selectById(postId);
        if (state != null) {
            vo.setLikeCount(state.getLikeCount());
            vo.setCommentCount(state.getCommentCount());
            vo.setViewCount(state.getViewCount());
            vo.setFavouriteCount(state.getFavouriteCount());
        }

        // 3. 填充当前用户的交互状态 (点赞、收藏、游览历史)
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null) {
            // 查询帖子是否点赞
            vo.setIsLiked(likesMapper.selectCount(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getUserId, currentUserId)
                    .eq(Likes::getTargetType, 0)
                    .eq(Likes::getTargetId, postId)) > 0 ? 1 : 0);

            // 查询帖子是否收藏
            vo.setIsFavourite(favouriteMapper.selectCount(new LambdaQueryWrapper<Favourite>()
                    .eq(Favourite::getUserId, currentUserId)
                    .eq(Favourite::getPostId, postId)) > 0 ? 1 : 0);

            // 查询是否关注作者
            vo.setIsFollowed(followsMapper.selectCount(new LambdaQueryWrapper<Follows>()
                    .eq(Follows::getUserId, currentUserId)
                    .eq(Follows::getFollowedId, post.getUserId())) > 0 ? 1 : 0);

            // 保存或更新游览历史记录
            ViewHistory viewHistory = new ViewHistory();
            viewHistory.setUserId(currentUserId);
            viewHistory.setPostId(postId);
            viewHistory.setViewTime(LocalDateTime.now());

            // 检查是否存在历史记录
            ViewHistory existing = viewHistoryMapper.selectOne(new LambdaQueryWrapper<ViewHistory>()
                    .eq(ViewHistory::getUserId, currentUserId)
                    .eq(ViewHistory::getPostId, postId));

            if (existing != null) {
                viewHistory.setViewId(existing.getViewId());
                viewHistoryMapper.updateById(viewHistory);
            } else {
                viewHistoryMapper.insert(viewHistory);
            }

        } else {
            // 游客状态
            vo.setIsLiked(0);
            vo.setIsFavourite(0);
        }

        return vo;
    }
}
