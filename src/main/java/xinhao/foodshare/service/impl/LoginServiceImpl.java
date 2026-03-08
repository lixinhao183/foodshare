package xinhao.foodshare.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import xinhao.foodshare.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.vo.LoginUser;
import xinhao.foodshare.service.LoginService;
import xinhao.foodshare.utils.JwtUtil;
import java.util.concurrent.TimeUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache  redisCache;

    /**
     * 用户登录
     * @param user 用户登录信息
     * @return 登录结果
     */
    @Override
    @Transactional
    public Map login(User user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //如果认证没通过，给出对应的提示
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        //如果认证通过，使用userid生成jwt token,jwt存入ResponseResult进行返回
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        String userid = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtil.createToken(userid);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);

        // 实现单点登录：先检查是否有其他活跃会话，如果有则删除
        String oldSessionKey = "login:" + userid;
        Object oldSession = redisCache.getCacheObject(oldSessionKey);
        if (oldSession != null) {
            // 如果用户已在其他地方登录，删除旧会话
            redisCache.deleteObject(oldSessionKey);
        }

        //把完整的用户信息存入redis,userid作为key
        redisCache.setCacheObject("login:" + userid, loginUser,(int)JwtUtil.JWT_TTL / 1000,TimeUnit.SECONDS);
        return map;
    }

    /**
     * 用户退出登录
     * @return 退出登录结果
     */
    @Override
    public void logout() {
        //获取SecurityContextHolder中的用户id
        Long userId = SecurityUtils.getUserId();
        //删除redis中的值
        redisCache.deleteObject("login:" + userId);
    }

}
