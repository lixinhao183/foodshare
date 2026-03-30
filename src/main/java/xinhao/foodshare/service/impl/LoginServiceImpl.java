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

import xinhao.foodshare.pojo.entity.permission.UserRole;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.UserRoleMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache  redisCache;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        //把完整的用户信息存入redis,userid作为key
        redisCache.setCacheObject("login:" + userid, loginUser,(int)JwtUtil.JWT_TTL / 1000,TimeUnit.SECONDS);
        return map;
    }

    /**
     * 游客登录
     * @return 登录结果
     */
    @Override
    @Transactional
    public Map guestLogin() {
        // 1. 生成唯一的游客用户名和默认密码
        String guestUsername = "guest_" + UUID.randomUUID().toString().substring(0, 8);
        String defaultPassword = "guestPassword";
        
        // 2. 创建并保存游客用户
        User user = new User();
        user.setUsername(guestUsername);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setRole(4); // 4-游客
        user.setStatus(0); // 0-启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        
        // 3. 分配角色 (角色ID为4)
        UserRole userRole = new UserRole(user.getUserId(), 4L);
        userRoleMapper.insert(userRole);
        
        // 4. 进行认证 (这里可以复用常规登录逻辑)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(guestUsername, defaultPassword);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("游客认证失败");
        }
        
        // 5. 生成Token并存入Redis
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtil.createToken(userId);
        
        redisCache.setCacheObject("login:" + userId, loginUser, (int) JwtUtil.JWT_TTL / 1000, TimeUnit.SECONDS);
        
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        map.put("username", guestUsername);
        return map;
    }

    /**
     * 用户退出登录
     * @return 退出登录结果
     */
    @Override
    @Transactional
    public void logout() {
        //获取当前登录用户的身份信息
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LoginUser)) {
            return;
        }

        LoginUser loginUser = (LoginUser) principal;
        User user = loginUser.getUser();
        
        if (user != null) {
            Long userId = user.getUserId();
            String username = user.getUsername();
            
            // 增强游客判断：角色为4 或者 用户名以 guest_ 开头
            boolean isGuest = (user.getRole() != null && user.getRole() == 4) || 
                             (username != null && username.startsWith("guest_"));
            
            log.info("用户退出登录: userId={}, username={}, isGuest={}", userId, username, isGuest);
            
            if (isGuest) {
                // 1. 删除用户角色关联
                LambdaQueryWrapper<UserRole> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.eq(UserRole::getUserId, userId);
                userRoleMapper.delete(roleWrapper);
                
                // 2. 删除用户信息
                userMapper.deleteById(userId);
                
                log.info("已删除游客账号信息: userId={}", userId);
            }
            
            // 3. 删除redis中的值
            redisCache.deleteObject("login:" + userId);
        }
    }

}
