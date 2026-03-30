package xinhao.foodshare.service;

import java.util.List;

import xinhao.foodshare.pojo.dto.PostDTO;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.dto.ReportDTO;
import xinhao.foodshare.pojo.dto.CommentDTO;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.vo.AnnouncementVO;
import xinhao.foodshare.pojo.vo.FollowsVO;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.PageResult;

public interface UserService {
    /**
     * 用户注册
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    UserVO register(UserRegisterDTO userRegisterDTO);

    /**
     * 用户更新信息
     * @param userUpdateDTO 用户更新信息
     */
    void update(UserUpdateDTO userUpdateDTO);

    /**
     * 用户获取个人信息
     * @param userId 用户ID (可选，如果为null则查询当前登录用户)
     * @return 用户个人信息
     */
    UserVO info(Long userId);

    /**
     * 查询关注用户
     * @return 关注用户列表
     */
    PageResult<FollowsVO> follow(Integer page, Integer pageSize);

    /**
     * 查询粉丝用户
     * @param page 页码
     * @param pageSize 每页数量
     * @return 粉丝用户列表
     */
    PageResult<FollowsVO> fans(Integer page, Integer pageSize);
    
    /**
     * 分页查询用户游览记录
     * @param page 页码
     * @param pageSize 每页数量
     * @return 用户游览记录对应的帖子列表
     */
    PageResult<PostVO> viewHistory(Integer page, Integer pageSize);

    /**
     * 清空游览记录
     */
    void clearViewHistory();

    /**
     * 删除单条游览记录
     * @param viewId 游览记录ID
     */
    void deleteViewHistory(Long viewId);

    /**
     * 分页查询收藏帖子
     * @param page 页码
     * @param pageSize 每页数量
     * @return 收藏帖子列表
     */
    PageResult<PostVO> favourite(Integer page, Integer pageSize);


    /**
     * 举报
     * @param reportDTO 举报信息
     */
    void report(ReportDTO reportDTO);

    /**
     * 发表评论
     * @param commentDTO 评论信息
     */
    void addComment(CommentDTO commentDTO);

    /**
     * 删除评论
     * @param commentId 评论ID
     */
    void deleteComment(Long commentId);



    /**
     * 根据帖子ID列表批量查询帖子信息
     * @param postIds 帖子ID列表
     * @return 帖子列表
     */
    List<PostVO> getPostsByIds(List<Long> postIds);

    /**
     * 发布帖子
     * @param postDTO 帖子数据
     * @return 帖子ID
     */
    Long publish(PostDTO postDTO);

    /**
     * 查询用户发布的帖子
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    PageResult<PostVO> getPosts(Long userId, Integer page, Integer pageSize);

    /**
     * 删除帖子
     * @param postId 帖子ID
     */
    void delete(Long postId);

    /**
     * 分页查询公告
     * @param page 页码
     * @param pageSize 每页数量
     * @return 公告列表
     */
    PageResult<AnnouncementVO> getAnnouncements(Integer page, Integer pageSize);

    /**
     * 用户点赞帖子
     * @param postId 帖子ID
     */
    void likePost(Long postId);

    /**
     * 用户取消点赞帖子
     * @param postId 帖子ID
     */
    void unlikePost(Long postId);

     /**
      * 用户点赞评论
      * @param commentId 评论ID
      */
    void likeComment(Long commentId);

    /**
     * 用户取消点赞评论
     * @param commentId 评论ID
     */
    void unlikeComment(Long commentId);

    /**
     * 用户收藏帖子
     * @param postId 帖子ID
     */
    void favoritePost(Long postId);

    /**
     * 用户取消收藏帖子
     * @param postId 帖子ID
     */
    void unFavoritePost(Long postId);

    /**
     * 用户关注用户
     * @param userId 用户ID
     */
    void follow(Long userId);

    /**
     * 用户取消关注用户
     * @param userId 用户ID
     */
    void unfollow(Long userId);
}

