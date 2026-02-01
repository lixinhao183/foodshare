package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import xinhao.foodshare.utils.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.service.UserService;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册用户
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @Override
    @Transactional
    public ResponseResult register(UserRegisterDTO userRegisterDTO) {
        //属性拷贝，将UserDTO转换为User实体类
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);

        // 检查必填字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User existingUser = userMapper.selectOne(wrapper);

        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 对密码进行加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 设置默认值
        user.setUserId(null);
        user.setRole(2); // 默认为普通用户
        user.setStatus(0); // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入新用户
        int result = userMapper.insert(user);

        if (result > 0) {
            // 注册成功，返回用户信息（不包含密码）
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            return ResponseResult.success("注册成功", userVO);
        } else {
            throw new RuntimeException("注册失败");
        }
    }


    /**
     * 更新用户信息
     * @param userUpdateDTO 用户更新信息
     */
    @Override
    @Transactional
    public ResponseResult update(UserUpdateDTO userUpdateDTO) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();

        //属性拷贝，将UserUpdateDTO转换为User实体类
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        user.setUserId(userId);

        // 如果要修改用户名，需要检查是否重复 (假设允许修改用户名)
        if (userUpdateDTO.getUsername() != null) {
             LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
             wrapper.eq(User::getUsername, userUpdateDTO.getUsername());
             // 排除自己
             wrapper.ne(User::getUserId, userId);
             User existingUser = userMapper.selectOne(wrapper);
             if (existingUser != null) {
                 throw new RuntimeException("用户名已存在");
             }
        }
        // 更新用户信息
        user.setUpdateTime(LocalDateTime.now());
        // MybatisPlus updateById 默认忽略 null 值
        int result = userMapper.updateById(user);

        if (result > 0) {
            return ResponseResult.success("更新用户信息成功");
        } else {
            throw new RuntimeException("更新用户信息失败");
        }
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @Override
    public ResponseResult<UserVO> info() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 转换为VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResponseResult.success("获取用户信息成功", userVO);
    }

    
}