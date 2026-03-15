package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xinhao.foodshare.pojo.dto.PostDTO;
import xinhao.foodshare.pojo.dto.ReportDTO;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.dto.CommentDTO;
import xinhao.foodshare.pojo.vo.AnnouncementVO;
import xinhao.foodshare.pojo.vo.FollowsVO;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.service.LoginService;
import xinhao.foodshare.service.UserService;

/*
* 用户管理
*/
@RestController
@Slf4j
@RequestMapping("/user")
@PreAuthorize("hasAuthority('system:user:list')")
public class UserController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * 
     * @param user 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ResponseResult login(@RequestBody User user) {
        log.info("用户登录:{}", user.getUsername());
        Map map = loginService.login(user);
        return ResponseResult.success(map);
    }

    /**
     * 用户退出登录
     * 
     * @return 退出登录结果
     */
    @GetMapping("/logout")
    public ResponseResult logout() {
        log.info("用户退出登录");
        loginService.logout();
        return ResponseResult.success("退出登录成功");
    }

    /**
     * 用户注册
     * 
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ResponseResult<UserVO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册:{}", userRegisterDTO);
        UserVO userVO = userService.register(userRegisterDTO);
        return ResponseResult.success(userVO);
    }

    /**
     * 用户更新信息
     * 
     * @param userUpdateDTO 用户更新信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public ResponseResult update(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("用户更新:{}", userUpdateDTO);
        userService.update(userUpdateDTO);
        return ResponseResult.success();
    }

    /**
     * 用户获取个人信息
     * 
     * @param userId 用户ID (可选，默认当前登录用户)
     * @return 用户个人信息
     */
    @GetMapping("/info")
    public ResponseResult<UserVO> info(@RequestParam(required = false) Long userId) {
        log.info("用户获取个人信息: userId={}", userId);
        UserVO userVO = userService.info(userId);
        return ResponseResult.success(userVO);
    }

    /**
     * 分页查询关注用户
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @return 关注用户列表
     */
    @GetMapping("/follows")
    public ResponseResult<PageResult<FollowsVO>> follow(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询关注用户");
        PageResult<FollowsVO> followsVOList = userService.follow(page, pageSize);
        return ResponseResult.success(followsVOList);
    }

    /**
     * 分页查询粉丝用户
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @return 粉丝用户列表
     */
    @GetMapping("/fans")
    public ResponseResult<PageResult<FollowsVO>> fans(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询粉丝用户");
        PageResult<FollowsVO> fansVOList = userService.fans(page, pageSize);
        return ResponseResult.success(fansVOList);
    }

    /**
     * 分页查询游览记录
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @return 游览记录列表
     */
    @GetMapping("/viewhistory")
    public ResponseResult<PageResult<PostVO>> viewHistory(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询游览记录");
        PageResult<PostVO> viewHistoryList = userService.viewHistory(page, pageSize);
        return ResponseResult.success(viewHistoryList);
    }

    /**
     * 分页查询收藏帖子
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @return 收藏帖子列表
     */
    @GetMapping("/favourite")
    public ResponseResult<PageResult<PostVO>> favourite(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询收藏帖子");
        PageResult<PostVO> postList = userService.favourite(page, pageSize);
        return ResponseResult.success(postList);
    }

    /**
     * 发布帖子
     * 
     * @param postDTO 帖子数据
     * @return 发布结果
     */
    @PostMapping("/post/publish")
    public ResponseResult<Long> publish(@RequestBody PostDTO postDTO) {
        log.info("用户发布帖子");
        Long postId = userService.publish(postDTO);
        return ResponseResult.success(postId);
    }

    /**
     * 分页查询用户发布的帖子记录
     * 
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    @GetMapping("/post")
    public ResponseResult<PageResult<PostVO>> posts(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询发布的帖子");
        PageResult<PostVO> postList = userService.getPosts(userId, page, pageSize);
        return ResponseResult.success(postList);
    }

    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @DeleteMapping("/post/{postId}")
    public ResponseResult deletePost(@PathVariable Long postId) {
        log.info("用户删除帖子: {}", postId);
        userService.delete(postId);
        return ResponseResult.success();
    }

    /**
     * 分页查询公告列表
     * 
     * @param page     页码
     * @param pageSize 每页数量
     * @return 公告列表
     */
    @GetMapping("/announcement")
    public ResponseResult<PageResult<AnnouncementVO>> getAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("用户查询公告列表: page={}, pageSize={}", page, pageSize);
        PageResult<AnnouncementVO> announcementList = userService.getAnnouncements(page, pageSize);
        return ResponseResult.success(announcementList);
    }

    /**
     * 举报
     * 
     * @param reportDTO 举报信息
     * @return 结果
     */
    @PostMapping("/report")
    public ResponseResult report(@RequestBody ReportDTO reportDTO) {
        log.info("用户提交举报: {}", reportDTO);
        userService.report(reportDTO);
        return ResponseResult.success();
    }

    /**
     * 发表评论
     * 
     * @param commentDTO 评论信息
     * @return 结果
     */
    @PostMapping("/comment")
    public ResponseResult addComment(@RequestBody CommentDTO commentDTO) {
        log.info("用户发表评论: {}", commentDTO);
        userService.addComment(commentDTO);
        return ResponseResult.success();
    }

    /**
     * 删除评论
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseResult deleteComment(@PathVariable Long commentId) {
        log.info("用户删除评论: {}", commentId);
        userService.deleteComment(commentId);
        return ResponseResult.success();
    }

    /**
     * 用户点赞帖子
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @PostMapping("/like/{postId}")
    public ResponseResult likePost(@PathVariable Long postId) {
        log.info("用户点赞帖子: {}", postId);
        userService.likePost(postId);
        return ResponseResult.success();
    }

    /**
     * 用户取消点赞帖子
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @DeleteMapping("/like/{postId}")
    public ResponseResult unlikePost(@PathVariable Long postId) {
        log.info("用户取消点赞帖子: {}", postId);
        userService.unlikePost(postId);
        return ResponseResult.success();
    }

    /**
     * 用户点赞评论
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    @PostMapping("/like/comment/{commentId}")
    public ResponseResult likeComment(@PathVariable Long commentId) {
        log.info("用户点赞评论: {}", commentId);
        userService.likeComment(commentId);
        return ResponseResult.success();
    }

    /**
     * 用户取消点赞评论
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    @DeleteMapping("/like/comment/{commentId}")
    public ResponseResult unlikeComment(@PathVariable Long commentId) {
        log.info("用户取消点赞评论: {}", commentId);
        userService.unlikeComment(commentId);
        return ResponseResult.success();
    }

    /**
     * 用户收藏帖子
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @PostMapping("/favourite/{postId}")
    public ResponseResult favoritePost(@PathVariable Long postId) {
        log.info("用户收藏帖子: {}", postId);
        userService.favoritePost(postId);
        return ResponseResult.success();
    }

    /**
     * 用户取消收藏帖子
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @DeleteMapping("/favourite/{postId}")
    public ResponseResult unFavoritePost(@PathVariable Long postId) {
        log.info("用户取消收藏帖子: {}", postId);
        userService.unFavoritePost(postId);
        return ResponseResult.success();
    }

    /**
     * 用户关注用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @PostMapping("/follow/{userId}")
    public ResponseResult follow(@PathVariable Long userId) {
        log.info("用户关注用户: {}", userId);
        userService.follow(userId);
        return ResponseResult.success();
    }

    /**
     * 用户取消关注用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/follow/{userId}")
    public ResponseResult unfollow(@PathVariable Long userId) {
        log.info("用户取消关注用户: {}", userId);
        userService.unfollow(userId);
        return ResponseResult.success();
    }
}
