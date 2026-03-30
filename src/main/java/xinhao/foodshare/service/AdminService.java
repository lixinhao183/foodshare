package xinhao.foodshare.service;

import java.util.List;
import xinhao.foodshare.pojo.dto.AnnouncementDTO;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.pojo.vo.ReportVO;
import xinhao.foodshare.pojo.vo.UserRoleInfoVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.PageResult;

public interface AdminService {

    /**
     * 分页查询举报记录
     * @param page 页码
     * @param pageSize 每页数量
     * @param isStatus 处理状态 (可选)
     * @return 举报记录列表
     */
    PageResult<ReportVO> getReportList(Integer page, Integer pageSize, Integer isStatus);

    /**
     * 处理举报记录
     * @param id 举报记录ID
     * @param isStatus 处理状态 (0未处理, 1已处理)
     */
    void handleReport(Long id, Integer isStatus);

    /**
     * 删除举报记录
     * @param id 举报记录ID
     */
    void deleteReport(Long id);


    /**
     * 发布公告
     * @param announcementDTO 公告信息
     */
    void publishAnnouncement(AnnouncementDTO announcementDTO);

    /**
     * 删除公告
     * @param id 公告ID
     */
    void deleteAnnouncement(Integer id);

    /**
     * 分页查询用户列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param username 用户名（可选，模糊查询）
     * @return 用户列表
     */
    PageResult<UserVO> getUserList(Integer page, Integer pageSize, String username);

    /**
     * 修改用户状态（封禁/解封）
     * @param userId 用户ID
     * @param status 状态（0启用，1禁用）
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 分页查询帖子列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param title 标题（可选，模糊查询）
     * @param status 状态（可选）
     * @return 帖子列表
     */
    PageResult<PostVO> getPostList(Integer page, Integer pageSize, String title, Integer status);

    /**
     * 分页查询评论列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param postId 帖子ID
     * @return 评论列表
     */
    PageResult<Comment> listComments(Integer page, Integer pageSize, Long postId);
    

     /**
     * 删除评论
     * @param commentId 评论ID
     */
    void deleteComment(Long commentId);

    /**
     * 审核帖子
     * @param postId 帖子ID
     * @param status 状态（1未通过，2已通过）
     */
    void updatePostStatus(Long postId, Integer status);

    /**
     * 删除帖子（逻辑删除）
     * @param postId 帖子ID
     */
    void deletePost(Long postId);

    /**
     * 添加标签
     * @param tags 标签列表
     */
    void addTag(List<Tag> tags);

    /**
     * 删除标签
     * @param id 标签ID
     */
    void deleteTag(Integer id);

    /**
     * 获取用户角色信息
     * @param userId 用户ID
     * @return 用户角色信息
     */
    UserRoleInfoVO getUserRoleInfo(Long userId);

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void updateUserRoles(Long userId, List<Long> roleIds);
}
