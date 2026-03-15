package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import xinhao.foodshare.pojo.dto.AnnouncementDTO;
import xinhao.foodshare.pojo.dto.UserRoleUpdateDTO;
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
@PreAuthorize("hasAuthority('system:dept:list')")
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
     * 审核帖子
     * @param postId 帖子ID
     * @param status 状态（1未通过，2已通过）
     * @return 结果
     */
    @PutMapping("/post/audit/{postId}/{status}")
    public ResponseResult auditPost(@PathVariable Long postId, @PathVariable Integer status) {
        log.info("管理员审核帖子: postId={}, status={}", postId, status);
        adminService.auditPost(postId, status);
        return ResponseResult.success();
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return 结果
     */
    @DeleteMapping("/post/{postId}")
    public ResponseResult deletePost(@PathVariable Long postId) {
        log.info("管理员删除帖子: postId={}", postId);
        adminService.deletePost(postId);
        return ResponseResult.success();
    }

    /**
     * 发布公告
     * @param announcementDTO 公告信息
     * @return 结果
     */
    @PostMapping("/announcement")
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
    public ResponseResult handleReport(@PathVariable Integer id) {
        log.info("管理员处理举报记录: id={}", id);
        adminService.handleReport(id);
        return ResponseResult.success();
    }

    /**
     * 批量新增标签
     * @param tags 标签列表
     * @return 结果
     */
    @PostMapping("/tag")
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
    public ResponseResult updateUserRoles(@RequestBody UserRoleUpdateDTO dto) {
        log.info("管理员修改用户角色: userId={}, roleIds={}", dto.getUserId(), dto.getRoleIds());
        adminService.updateUserRoles(dto.getUserId(), dto.getRoleIds());
        return ResponseResult.success();
    }
}
