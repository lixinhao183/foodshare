package xinhao.foodshare.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.vo.LoginUser;

public class SecurityUtils {

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        return (LoginUser) getAuthentication().getPrincipal();
    }
    
    /**
     * 获取用户实体类
     */
    public static User getUser() {
        return (User) getLoginUser().getUser();
    }


    /**
     * 获取Authentication（当前登录用户的认证信息）
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return getLoginUser().getUser().getUserId();
    }
}