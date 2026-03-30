package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import xinhao.foodshare.pojo.dto.AnnouncementDTO;
import xinhao.foodshare.pojo.dto.UserRoleUpdateDTO;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.vo.UserRoleInfoVO;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.pojo.vo.ReportVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.service.AdminService;

/**
 * 管理员管理模块
 */
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 分页查询用户列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param username 用户名（可选）
     * @return 用户列表
     */
    @GetMapping("/user/list")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult<PageResult<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String username) {
        log.info("管理员查询用户列表: page={}, pageSize={}, username={}", page, pageSize, username);
        PageResult<UserVO> userList = adminService.getUserList(page, pageSize, username);
        return ResponseResult.success(userList);
    }

    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 状态（0启用，1禁用）
     * @return 结果
     */
    @PutMapping("/user/status/{userId}/{status}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult updateUserStatus(@PathVariable Long userId, @PathVariable Integer status) {
        log.info("管理员修改用户状态: userId={}, status={}", userId, status);
        adminService.updateUserStatus(userId, status);
        return ResponseResult.success();
    }

    /**
     * 分页查询帖子列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param title 标题（可选）
     * @param status 状态（可选）
     * @return 帖子列表
     */
    @GetMapping("/post/list")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult<PageResult<PostVO>> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String title,
            Integer status) {
        log.info("管理员查询帖子列表: page={}, pageSize={}, title={}, status={}", page, pageSize, title, status);
        PageResult<PostVO> postList = adminService.getPostList(page, pageSize, title, status);
        return ResponseResult.success(postList);
    }

    /**
     * 分页查询评论列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param postId 帖子ID（可选）
     * @return 评论列表
     */
    @GetMapping("/comment/list")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult<PageResult<Comment>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Long postId) {
        log.info("管理员查询评论列表: page={}, pageSize={}, postId={}", page, pageSize, postId);
        PageResult<Comment> commentList = adminService.listComments(page, pageSize, postId);
        return ResponseResult.success(commentList);
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return 结果
     */
    @DeleteMapping("/post/{postId}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult deletePost(@PathVariable Long postId) {
        log.info("管理员删除帖子: postId={}", postId);
        adminService.deletePost(postId);
        return ResponseResult.success();
    }
    
    /**
     * 审核帖子
     * @param postId 帖子ID
     * @param status 状态（0待审核，1未通过，2已通过）
     * @return 结果
     */
    @PutMapping("/post/status/{postId}/{status}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult updatePostStatus(@PathVariable Long postId, @PathVariable Integer status) {
        log.info("管理员审核帖子: postId={}, status={}", postId, status);
        adminService.updatePostStatus(postId, status);
        return ResponseResult.success();
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 结果
     */
    @DeleteMapping("/comment/{commentId}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult deleteComment(@PathVariable Long commentId) {
        log.info("管理员删除评论: commentId={}", commentId);
        adminService.deleteComment(commentId);
        return ResponseResult.success();
    }

    /**
     * 发布公告
     * @param announcementDTO 公告信息
     * @return 结果
     */
    @PostMapping("/announcement")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult publishAnnouncement(@RequestBody AnnouncementDTO announcementDTO) {
        log.info("管理员发布公告: {}", announcementDTO);
        adminService.publishAnnouncement(announcementDTO);
        return ResponseResult.success();
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @return 结果
     */
    @DeleteMapping("/announcement/{id}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult deleteAnnouncement(@PathVariable Integer id) {
        log.info("管理员删除公告: {}", id);
        adminService.deleteAnnouncement(id);
        return ResponseResult.success();
    }

    /**
     * 分页查询举报记录
     * @param page 页码
     * @param pageSize 每页数量
     * @param isStatus 处理状态 (可选)
     * @return 举报记录列表
     */
    @GetMapping("/report/list")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult<PageResult<ReportVO>> getReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Integer isStatus) {
        log.info("管理员查询举报记录: page={}, pageSize={}, isStatus={}", page, pageSize, isStatus);
        PageResult<ReportVO> reportList = adminService.getReportList(page, pageSize, isStatus);
        return ResponseResult.success(reportList);
    }

    /**
     * 处理举报记录
     * @param id 举报记录ID
     * @param isStatus 处理状态 (0未处理, 1已处理)
     * @return 结果
     */
    @PutMapping("/report/{id}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult handleReport(@PathVariable Long id, @RequestParam(required = false) Integer isStatus) {
        log.info("管理员处理举报记录: id={}, isStatus={}", id, isStatus);
        adminService.handleReport(id, isStatus);
        return ResponseResult.success();
    }

    /**
     * 删除举报记录
     * @param id 举报记录ID
     * @return 结果
     */
    @DeleteMapping("/report/{id}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult deleteReport(@PathVariable Long id) {
        log.info("管理员删除举报记录: id={}", id);
        adminService.deleteReport(id);
        return ResponseResult.success();
    }

    /**
     * 批量新增标签
     * @param tags 标签列表
     * @return 结果
     */
    @PostMapping("/tag")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult addTag(@RequestBody List<Tag> tags) {
        log.info("管理员新增标签: {}", tags);
        adminService.addTag(tags);
        return ResponseResult.success();
    }

    /**
     * 删除标签
     * @param id 标签ID
     * @return 结果
     */
    @DeleteMapping("/tag/{id}")
    @PreAuthorize("hasAuthority('admin:manage')")
    public ResponseResult deleteTag(@PathVariable Integer id) {
        log.info("管理员删除标签: {}", id);
        adminService.deleteTag(id);
        return ResponseResult.success();
    }

    /**
     * 查看用户角色
     * @param userId 用户ID
     * @return 用户角色信息
     */
    @GetMapping("/user/role")
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseResult<UserRoleInfoVO> getUserRoleInfo(@RequestParam Long userId) {
        log.info("管理员查看用户角色: userId={}", userId);
        UserRoleInfoVO vo = adminService.getUserRoleInfo(userId);
        return ResponseResult.success(vo);
    }

    /**
     * 修改用户角色
     * @param dto 用户角色更新DTO
     * @return 结果
     */
    @PutMapping("/user/role")
    @PreAuthorize("hasAuthority('permission:manage')")
    public ResponseResult updateUserRoles(@RequestBody UserRoleUpdateDTO dto) {
        log.info("管理员修改用户角色: userId={}, roleIds={}", dto.getUserId(), dto.getRoleIds());
        adminService.updateUserRoles(dto.getUserId(), dto.getRoleIds());
        return ResponseResult.success();
    }
}
