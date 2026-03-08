package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import xinhao.foodshare.utils.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.mapper.FavouriteMapper;
import xinhao.foodshare.mapper.FollowsMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.ViewHistoryMapper;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.Favourite;
import xinhao.foodshare.pojo.entity.Follows;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.ViewHistory;
import xinhao.foodshare.pojo.vo.FollowsVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowsMapper followsMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ViewHistoryMapper viewHistoryMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Autowired
    private PostMapper postMapper;
    

    /**
     * 注册用户
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @Override
    public UserVO register(UserRegisterDTO userRegisterDTO) {
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

            return userVO;
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
    public void update(UserUpdateDTO userUpdateDTO) {
        // 获取当前登录用户
        User user = SecurityUtils.getUser();

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果要修改用户名，需要检查是否重复 (假设允许修改用户名)
        if (userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().equals(user.getUsername())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, userUpdateDTO.getUsername());
            User existingUser = userMapper.selectOne(wrapper);
            if (existingUser != null) {
                throw new RuntimeException("用户名已存在");
            }
        }

        // 属性拷贝，将UserUpdateDTO转换为User实体类
        BeanUtils.copyProperties(userUpdateDTO, user);

        // 更新时间
        user.setUpdateTime(LocalDateTime.now());

        // 更新用户信息
        userMapper.updateById(user);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @Override
    public UserVO info() {
        User user = SecurityUtils.getUser();
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 转换为VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        return userVO;
    }


    /**
     * 查询关注用户
     * @return 关注用户列表
     */
    @Override
    public List<FollowsVO> follow() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        
        // 从Redis缓存中获取关注用户列表
        List<FollowsVO> followsVOList = redisCache.getCacheList("follows:" + userId);
        if (followsVOList == null || followsVOList.isEmpty()) {
        
            // 查询关注表，找到当前用户关注的所有记录
            LambdaQueryWrapper<Follows> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Follows::getUserId, userId);
            List<Follows> followsList = followsMapper.selectList(queryWrapper);
            
            if (followsList.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 提取被关注者的ID列表
            List<Long> followedIds = followsList.stream()
                    .map(Follows::getFollowedId)
                    .collect(Collectors.toList());
            
            // 根据ID列表批量查询用户信息
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(User::getUserId, followedIds);
            List<User> userList = userMapper.selectList(userWrapper);

            // 构建Map, Key为被关注者ID, Value为备注名
            Map<Long, String> remarkMap = followsList.stream()
                    .collect(Collectors.toMap(Follows::getFollowedId, follows -> follows.getRemarkName() != null ? follows.getRemarkName() : ""));

            // 转换为VO对象
            followsVOList = userList.stream().map(user -> {
                FollowsVO followsVO = new FollowsVO();
                BeanUtils.copyProperties(user, followsVO);
                // 设置备注名
                followsVO.setRemarkName(remarkMap.get(user.getUserId()));
                return followsVO;
            }).collect(Collectors.toList());

            // 存储到Redis
            redisCache.setCacheList("follows:" + userId, followsVOList);
        }
    return followsVOList;
    }


    /**根据帖子id列表查询帖子信息
     * @param postIds 帖子ID列表
     * @return 帖子信息列表
     */
    @Override
    public List<Post> queryPostsByIds(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据ID列表批量查询帖子信息
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getPostId, postIds)
                .select(Post::getPostId, Post::getTitle, Post::getImages, Post::getLikeCount, Post::getViewCount, Post::getCreateTime, Post::getUserId);

        List<Post> postList = postMapper.selectList(postWrapper);

        if (postList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 存储到Redis
        redisCache.setCacheList("posts:" + postIds, postList);
    return postList;
    }



    /**
     * 查询游览记录
     * @return 游览记录列表
     */
    @Override
    public List<ViewHistory> viewHistory() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        
        // 从Redis缓存中获取游览记录列表
        List<ViewHistory> viewHistoryList = redisCache.getCacheList("viewHistory:" + userId);
        if (viewHistoryList == null || viewHistoryList.isEmpty()) {
        
            // 查询游览记录表，找到当前用户的所有游览记录
            LambdaQueryWrapper<ViewHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ViewHistory::getUserId, userId)
                    .orderByDesc(ViewHistory::getViewTime);
            viewHistoryList = viewHistoryMapper.selectList(queryWrapper);
            
            if (viewHistoryList.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 存储到Redis
            redisCache.setCacheList("viewHistory:" + userId, viewHistoryList);
        }
    return viewHistoryList;
    }


    /**
     * 查询收藏帖子
     * @return 收藏帖子列表
     */
    @Override
    public List<Post> favourite() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
        
        // 从Redis缓存中获取收藏帖子列表
        List<Post> postList = redisCache.getCacheList("favourite:" + userId);
        if (postList == null || postList.isEmpty()) {
        
            // 查询收藏表，找到当前用户收藏的所有记录
            LambdaQueryWrapper<Favourite> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Favourite::getUserId, userId)
                    .orderByDesc(Favourite::getCreateTime);
            List<Favourite> favouriteList = favouriteMapper.selectList(queryWrapper);
            
            if (favouriteList.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 提取帖子ID列表
            List<Long> postIds = favouriteList.stream()
                    .map(Favourite::getPostId)
                    .collect(Collectors.toList());
            
            // 根据ID列表批量查询帖子信息
            LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
            postWrapper.in(Post::getPostId, postIds)
                    .select(Post::getPostId, Post::getTitle, Post::getImages, Post::getLikeCount, Post::getViewCount, Post::getCreateTime, Post::getUserId);

            postList = postMapper.selectList(postWrapper);

            if (postList.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 存储到Redis
            redisCache.setCacheList("favourite:" + userId, postList);
        }
    return postList;
    }

    

}
