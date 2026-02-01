package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.User;
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
        return loginService.login(user);
    }

    /**
     * 用户退出登录
     * @return 退出登录结果
     */
    @GetMapping("/logout")
    public ResponseResult logout() {
        log.info("用户退出登录:{}");
        return loginService.logout();
    }

    /**
     * 用户注册
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseResult<UserVO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册:{}", userRegisterDTO);
        return userService.register(userRegisterDTO);
    }

    /**
     * 用户更新信息
     * @param userUpdateDTO 用户更新信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public ResponseResult<Void> update(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("用户更新:{}", userUpdateDTO);
        return userService.update(userUpdateDTO);
    }

    /**
     * 用户获取个人信息
     * @return 用户个人信息
     */
    @GetMapping("/info")
    public ResponseResult<UserVO> info() {
        return userService.info();
    }
    
}
