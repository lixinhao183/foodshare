package xinhao.foodshare.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xinhao.foodshare.mapper.FavouriteMapper;
import xinhao.foodshare.mapper.LikesMapper;
import xinhao.foodshare.mapper.PostStateMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.pojo.entity.*;
import xinhao.foodshare.pojo.vo.PostVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 帖子数据处理工具类
 * 用于填充帖子列表的关联信息（作者、统计数据、用户交互状态）
 */
@Component
public class PostUtils {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostStateMapper postStateMapper;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 Post 列表转换为 PostVO 列表，并填充关联信息
     *
     * @param postList 原始帖子列表
     * @return 填充后的 PostVO 列表
     */
    public List<PostVO> fillPostDetails(List<Post> postList) {
        if (postList == null || postList.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取帖子ID列表
        List<Long> postIds = postList.stream().map(Post::getPostId).collect(Collectors.toList());

        // 1. 批量查询关联信息
        // 从帖子列表中提取所有作者ID
        Set<Long> userIds = postList.stream().map(Post::getUserId).collect(Collectors.toSet());

        // 一次性查询所有相关用户
        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getUserId, userIds))
                    .stream()
                    .collect(Collectors.toMap(User::getUserId, u -> u));
        }

        // 批量查询帖子统计信息
        Map<Long, PostState> stateMap = new HashMap<>();
        List<PostState> postStates = postStateMapper.selectList(
                new LambdaQueryWrapper<PostState>().in(PostState::getPostId, postIds));
        stateMap = postStates.stream()
                .collect(Collectors.toMap(PostState::getPostId, s -> s));

        // 批量查询当前用户的点赞和收藏状态
        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> favouritePostIds = new HashSet<>();
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null) {
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

        // 2. 数据组装 (Entity -> VO)
        final Map<Long, PostState> finalStateMap = stateMap;
        final Set<Long> finalLikedPostIds = likedPostIds;
        final Set<Long> finalFavouritePostIds = favouritePostIds;
        final Map<Long, User> finalUserMap = userMap;

        return postList.stream().map(post -> {
            PostVO vo = new PostVO();
            // 1. 拷贝帖子基本信息
            BeanUtils.copyProperties(post, vo, "images", "tags");

            // 处理图片 JSON -> List
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                try {
                    List<String> images = objectMapper.readValue(post.getImages(), new TypeReference<List<String>>() {});
                    vo.setImages(images);
                } catch (Exception e) {
                    // 如果解析失败，可能是旧数据或格式错误，视为空列表或不做处理
                    vo.setImages(new ArrayList<>());
                }
            } else {
                vo.setImages(new ArrayList<>());
            }

            // 处理标签 JSON -> List
            if (post.getTag() != null && !post.getTag().isEmpty()) {
                try {
                    List<String> tags = objectMapper.readValue(post.getTag(), new TypeReference<List<String>>() {});
                    vo.setTags(tags);
                } catch (Exception e) {
                    vo.setTags(new ArrayList<>());
                }
            } else {
                vo.setTags(new ArrayList<>());
            }

            // 2. 填充作者信息
            User user = finalUserMap.get(post.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getImage());
                vo.setUserId(user.getUserId());
            }

            // 3. 填充统计数据
            PostState state = finalStateMap.get(post.getPostId());
            if (state != null) {
                vo.setLikeCount(state.getLikeCount());
                vo.setCommentCount(state.getCommentCount());
                vo.setViewCount(state.getViewCount());
                vo.setFavouriteCount(state.getFavouriteCount());
            } else {
                vo.setLikeCount(0L);
                vo.setCommentCount(0L);
                vo.setViewCount(0L);
                vo.setFavouriteCount(0L);
            }

            // 4. 填充用户交互状态
            if (currentUserId != null) {
                vo.setIsLiked(finalLikedPostIds.contains(post.getPostId()) ? 1 : 0);
                vo.setIsFavourite(finalFavouritePostIds.contains(post.getPostId()) ? 1 : 0);
            } else {
                vo.setIsLiked(0);
                vo.setIsFavourite(0);
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
