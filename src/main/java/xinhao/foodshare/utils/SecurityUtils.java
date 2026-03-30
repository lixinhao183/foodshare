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
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LoginUser)) {
            throw new RuntimeException("用户未登录");
        }
        
        return (LoginUser) principal;
    }
    
    /**
     * 获取用户实体类
     */
    public static User getUser() {
        LoginUser loginUser = getLoginUser();
        return loginUser.getUser();
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