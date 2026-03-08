package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.ViewHistory;
import xinhao.foodshare.pojo.vo.FollowsVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.service.LoginService;
import xinhao.foodshare.service.UserService;


/*
* 用户管理
*/
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param user 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        log.info("用户登录:{}", user.getUsername());
        Map map = loginService.login(user);
        return ResponseResult.success(map);
    }

    /**
     * 用户退出登录
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
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseResult<UserVO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册:{}", userRegisterDTO);
        UserVO userVO = userService.register(userRegisterDTO);
        return ResponseResult.success(userVO);
    }

    /**
     * 用户更新信息
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
     * @return 用户个人信息
     */
    @GetMapping("/info")
    public ResponseResult<UserVO> info() {
        log.info("用户获取个人信息");
        UserVO userVO = userService.info();
        return ResponseResult.success(userVO);
    }
    
    /**
     * 查询关注用户
     * @return 关注用户列表
     */
    @GetMapping("/follows")
    public ResponseResult<List<FollowsVO>> follow() {
        log.info("用户查询关注用户");
        List<FollowsVO> followsVOList = userService.follow();
        return ResponseResult.success(followsVOList);
    }

    /**
     * 查询游览记录
     * @return 游览记录列表
     */
    @GetMapping("/viewhistory")
    public ResponseResult<List<ViewHistory>> viewHistory() {
        log.info("用户查询游览记录");
        List<ViewHistory> viewHistoryList = userService.viewHistory();
        return ResponseResult.success(viewHistoryList);
    }

     /**
      * 查询收藏帖子
      * @return 收藏帖子列表
      */
     @GetMapping("/favourite")
     public ResponseResult<List<Post>> favourite() {
        log.info("用户查询收藏帖子");
        List<Post> postList = userService.favourite();
        return ResponseResult.success(postList);
     }
}


