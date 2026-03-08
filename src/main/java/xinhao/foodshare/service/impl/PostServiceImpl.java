package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.mapper.CommentMapper;
import xinhao.foodshare.mapper.FavouriteMapper;
import xinhao.foodshare.mapper.LikesMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.PostStateMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.ViewHistoryMapper;
import xinhao.foodshare.pojo.dto.PostDTO;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Favourite;
import xinhao.foodshare.pojo.entity.Likes;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.PostState;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.ViewHistory;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.service.PostService;
import xinhao.foodshare.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * 分页查询帖子列表
     * 用于前端首页或发现页的无限加载流
     *
     * @param page     当前页码
     * @param pageSize 每页显示数量
     * @param sort     排序方式，"new" 按发布时间倒序，"hot" 按点赞数倒序
     * @return 封装好的分页结果，包含帖子详情、作者信息及当前用户的点赞状态
     */

    @Override
    public PageResult<PostVO> pageQuery(Integer page, Integer pageSize, String sort, String local) {
        // 构造缓存key
        String cacheKey = "post_page:" + sort + ":" + page + ":" + pageSize + ":" + local;

        // 1. 尝试从缓存获取 (只有查询热门帖子时才使用缓存)
        PageResult<PostVO> result = null;
        if ("hot".equals(sort)) {
            result = redisCache.getCacheObject(cacheKey);
        }

        // 2. 如果缓存未命中，查询数据库
        if (result == null) {
            // 1. 基础分页查询
            Page<Post> postPage = new Page<>(page, pageSize);
            LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
            // 过滤条件：只能看到审核通过(status=2) 且 未被删除(isDeleted=0) 的帖子
            queryWrapper.eq(Post::getStatus, 2)
                    .eq(Post::getIsDeleted, 0);

            // 如果有位置参数，添加位置过滤条件
            if (local != null && !local.isEmpty()) {
                queryWrapper.eq(Post::getLocal, local);
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

            // 2. 批量查询作者信息
            // 从帖子列表中提取所有作者ID
            Set<Long> userIds = records.stream()
                    .map(Post::getUserId)
                    .collect(Collectors.toSet());

            // 一次性查询所有相关用户，并转为 Map<UserId, User> 方便后续快速查找
            Map<Long, User> userMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getUserId, userIds))
                    .stream()
                    .collect(Collectors.toMap(User::getUserId, u -> u));

            // 提取当前页所有帖子的ID
            List<Long> postIds = records.stream().map(Post::getPostId).collect(Collectors.toList());

            // 批量查询帖子统计信息 (点赞数、评论数，收藏数，游览数)
            Map<Long, PostState> stateMap = new HashMap<>();
            if (!postIds.isEmpty()) {
                List<PostState> postStates = postStateMapper.selectList(
                        new LambdaQueryWrapper<PostState>().in(PostState::getPostId, postIds));
                stateMap = postStates.stream()
                        .collect(Collectors.toMap(PostState::getPostId, s -> s));
            }

            // 批量查询当前用户的点赞和收藏状态
            Set<Long> likedPostIds = new java.util.HashSet<>();
            Set<Long> favouritePostIds = new java.util.HashSet<>();
            Long currentUserId = SecurityUtils.getUserId();
            if (currentUserId != null && !postIds.isEmpty()) {
                // 批量查询点赞
                List<Likes> likesList = likesMapper.selectList(new LambdaQueryWrapper<Likes>()
                        .eq(Likes::getUserId, currentUserId)
                        .eq(Likes::getTargetType, 0)
                        .in(Likes::getTargetId, postIds));
                likedPostIds = likesList.stream().map(Likes::getTargetId).collect(Collectors.toSet());

                // 批量查询收藏
                List<Favourite> favouriteList = favouriteMapper.selectList(new LambdaQueryWrapper<Favourite>()
                        .eq(Favourite::getUserId, currentUserId)
                        .in(Favourite::getPostId, postIds));
                favouritePostIds = favouriteList.stream().map(Favourite::getPostId).collect(Collectors.toSet());
            }

            // 4. 数据组装 (Entity -> VO)
            List<PostVO> vos = new ArrayList<>();
            for (Post post : records) {
                PostVO vo = new PostVO();
                // 1. 拷贝帖子基本信息
                BeanUtils.copyProperties(post, vo);

                // 2. 填充作者信息 (头像、用户名)
                User user = userMap.get(post.getUserId());
                if (user != null) {
                    vo.setUsername(user.getUsername());
                    vo.setAvatar(user.getImage());
                    vo.setUserId(user.getUserId());
                }

                // 3. 填充统计数据
                PostState state = stateMap.get(post.getPostId());
                if (state != null) {
                    vo.setLikeCount(state.getLikeCount());
                    vo.setCommentCount(state.getCommentCount());
                    vo.setViewCount(state.getViewCount());
                    vo.setFavouriteCount(state.getFavouriteCount());
                }

                // 4. 填充用户交互状态
                if (currentUserId != null) {
                    vo.setIsLiked(likedPostIds.contains(post.getPostId()) ? 1 : 0);
                    vo.setIsFavourite(favouritePostIds.contains(post.getPostId()) ? 1 : 0);
                } else {
                    vo.setIsLiked(0);
                    vo.setIsFavourite(0);
                }

                vos.add(vo);
            }

            result = new PageResult<>(vos, postPage.getTotal());

            // 存入缓存，设置较短的过期时间（例如 30 分钟），保证列表时效性 (只有查询热门帖子时才缓存)
            if ("hot".equals(sort)) {
                redisCache.setCacheObject(cacheKey, result, 30, TimeUnit.MINUTES);
            }
        }

        return result;
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
        BeanUtils.copyProperties(post, vo);

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
                .eq(Comment::getParentId, 0)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(commentWrapper);

        // 填充评论用户信息和点赞数
        for (Comment comment : comments) {
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
        vo.setComments(comments);

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

    /**
     * 发布帖子
     * 
     * @param postDTO 帖子数据
     */
    @Override
    @Transactional
    public void publish(PostDTO postDTO) {
        // 1. 校验帖子数据
        if (postDTO == null || postDTO.getTitle() == null || postDTO.getTitle().isEmpty() ||
                postDTO.getContent() == null || postDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("帖子标题和内容不能为空");
        }

        // 2. 将 DTO 转换为 Entity
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post);

        // 3. 设置帖子作者为当前登录用户
        post.setUserId(SecurityUtils.getUserId());
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());

        // 默认为未审核状态 (status=0)
        // post.setStatus(0);

        post.setIsDeleted(0);
        post.setLikeCount(0L);
        post.setCommentCount(0L);
        post.setViewCount(0L);
        post.setFavouriteCount(0L);

        // 4. 插入数据库
        postMapper.insert(post);
        // 5. 初始化帖子统计信息

        PostState postState = new PostState();
        postState.setPostId(post.getPostId());
        postState.setLikeCount(0L);
        postState.setCommentCount(0L);
        postState.setViewCount(0L);
        postState.setFavouriteCount(0L);
        postState.setLastUpdate(LocalDateTime.now());

        postStateMapper.insert(postState);

    }


    /**
     * 查询用户发布的帖子
     * @param userId 用户ID
     * @return 帖子列表
     */
    @Override
    public List<Post> getPosts(Long userId) {

        // 从数据库中查询用户发布的帖子
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreateTime);
        List<Post> postList = postMapper.selectList(queryWrapper);
        
        if (postList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return postList;
    }
}
