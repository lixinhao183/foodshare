package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.mapper.CommentMapper;
import xinhao.foodshare.mapper.FavouriteMapper;
import xinhao.foodshare.mapper.LikesMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.PostStateMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Favourite;
import xinhao.foodshare.pojo.entity.Likes;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.PostState;
import xinhao.foodshare.pojo.entity.User;
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

    /**
     * 获取当前登录用户ID
     * @return 用户ID，如果未登录则返回null
     */
    private Long getCurrentUserId() {
        try {
            return SecurityUtils.getUserId();
        } catch (Exception e) {
            return null;
        }
    }

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
    public PageResult<PostVO> pageQuery(Integer page, Integer pageSize, String sort) {
        // 构造缓存key
        String cacheKey = "POST_PAGE:" + sort + ":" + page + ":" + pageSize;
        
        // 1. 尝试从缓存获取
        PageResult<PostVO> result = redisCache.getCacheObject(cacheKey);
        
        // 2. 如果缓存未命中，查询数据库
        if (result == null) {
            // 1. 基础分页查询
            Page<Post> postPage = new Page<>(page, pageSize);
            LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
            // 过滤条件：只能看到审核通过(status=2) 且 未被删除(isDeleted=0) 的帖子
            queryWrapper.eq(Post::getStatus, 2)
                        .eq(Post::getIsDeleted, 0)
                        // 排序：按发布时间倒序，最新的在前
                        .orderByDesc(Post::getCreateTime);
            // 根据排序参数添加排序条件
            if ("new".equals(sort)) {
                queryWrapper.orderByDesc(Post::getCreateTime);
            } else if ("hot".equals(sort)) {
                queryWrapper.orderByDesc(Post::getLikeCount);
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
                    new LambdaQueryWrapper<PostState>().in(PostState::getPostId, postIds)
                );
                stateMap = postStates.stream()
                        .collect(Collectors.toMap(PostState::getPostId, s -> s));
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
                
                // 缓存前先设置交互状态为 false
                vo.setIsLiked(false);
                vo.setIsFavourite(false);
                
                vos.add(vo);
            }
            
            result = new PageResult<>(vos, postPage.getTotal());
            
            // 存入缓存，设置较短的过期时间（例如 30 秒），保证列表时效性
            redisCache.setCacheObject(cacheKey, result, 30, TimeUnit.SECONDS);
        }

        // 3. 填充当前用户的交互状态 (用于判断是否点赞)
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null && result.getRecords() != null && !result.getRecords().isEmpty()) {
            List<PostVO> vos = result.getRecords();
            List<Long> postIds = vos.stream().map(PostVO::getPostId).collect(Collectors.toList());
            
            // 批量查询当前用户的点赞状态
            LambdaQueryWrapper<Likes> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(Likes::getUserId, currentUserId)
                       .eq(Likes::getTargetType, 0) // targetType: 0代表帖子, 1代表评论
                       .in(Likes::getTargetId, postIds);
            List<Likes> likes = likesMapper.selectList(likeWrapper);
            Set<Long> likedPostIds = likes.stream().map(Likes::getTargetId).collect(Collectors.toSet());
            
            // 批量查询当前用户的收藏状态
            LambdaQueryWrapper<Favourite> favouriteWrapper = new LambdaQueryWrapper<>();
            favouriteWrapper.eq(Favourite::getUserId, currentUserId)
                            .in(Favourite::getPostId, postIds);
            List<Favourite> favourites = favouriteMapper.selectList(favouriteWrapper);
            Set<Long> favouritePostIds = favourites.stream().map(Favourite::getPostId).collect(Collectors.toSet());
            
            // 更新VO中的状态
            for (PostVO vo : vos) {
                vo.setIsLiked(likedPostIds.contains(vo.getPostId()));
                vo.setIsFavourite(favouritePostIds.contains(vo.getPostId()));
            }
        } else if (result.getRecords() != null) {
            // 游客状态或列表为空
            for (PostVO vo : result.getRecords()) {
                vo.setIsLiked(false);
                vo.setIsFavourite(false);
            }
        }

        return result;
    }


    /**
     * 查询帖子详情
     * @param postId 帖子ID
     * @return 帖子详情数据
     */
    @Override
    public PostVO detail(Long postId) {
        // 1. 尝试从 Redis 获取缓存
        PostVO vo = redisCache.getCacheObject("POST_DETAIL_KEY:" + postId);
        
        // 2. 如果缓存未命中，查询数据库并重建缓存
        if (vo == null) {
            Post post = this.getById(postId);
            if (post == null || post.getIsDeleted() == 1 || post.getStatus() != 2) {
                throw new RuntimeException("帖子不存在或已被删除");
            }

            // 转换为VO
            vo = new PostVO();
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
                
                // 缓存时，将用户交互状态置为 null 或 false，防止缓存污染
                comment.setIsLiked(false);
            }
            vo.setComments(comments);
            
            // 缓存时，将用户交互状态置为 null 或 false
            vo.setIsLiked(false);
            vo.setIsFavourite(false);

            // 存入 Redis，设置过期时间（例如 30 分钟）
            redisCache.setCacheObject("POST_DETAIL_KEY:" + postId, vo, 30, TimeUnit.MINUTES);
        }

        // 3. 填充当前用户的交互状态 (点赞、收藏)
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            // 查询帖子是否点赞
            LambdaQueryWrapper<Likes> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(Likes::getUserId, currentUserId)
                    .eq(Likes::getTargetType, 0)
                    .eq(Likes::getTargetId, postId);
            vo.setIsLiked(likesMapper.selectCount(likeWrapper) > 0);

            // 查询帖子是否收藏
            LambdaQueryWrapper<Favourite> favouriteWrapper = new LambdaQueryWrapper<>();
            favouriteWrapper.eq(Favourite::getUserId, currentUserId)
                    .eq(Favourite::getPostId, postId);
            vo.setIsFavourite(favouriteMapper.selectCount(favouriteWrapper) > 0);
            
            // 填充评论的用户交互状态
            if (vo.getComments() != null) {
                for (Comment comment : vo.getComments()) {
                    LambdaQueryWrapper<Likes> isLikedWrapper = new LambdaQueryWrapper<>();
                    isLikedWrapper.eq(Likes::getUserId, currentUserId)
                                  .eq(Likes::getTargetType, 1)
                                  .eq(Likes::getTargetId, comment.getCommentId());
                    comment.setIsLiked(likesMapper.selectCount(isLikedWrapper) > 0);
                }
            }
        } else {
            // 游客状态
            vo.setIsLiked(false);
            vo.setIsFavourite(false);
            if (vo.getComments() != null) {
                for (Comment comment : vo.getComments()) {
                    comment.setIsLiked(false);
                }
            }
        }

        return vo;
    }

}
