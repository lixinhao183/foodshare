package xinhao.foodshare.service;

import java.util.Map;

import xinhao.foodshare.pojo.entity.User;

public interface LoginService {
    /**
     * 用户登录
     * @param user 用户登录信息
     * @return 登录结果
     */
    Map login(User user);

    /**
     * 游客登录
     * @return 登录结果
     */
    Map guestLogin();

    /**
     * 用户注销登录
     * @return 注销结果
     */
    void logout();
}
