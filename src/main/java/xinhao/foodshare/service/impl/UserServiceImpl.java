package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import xinhao.foodshare.utils.PostUtils;
import xinhao.foodshare.utils.SecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.mapper.CommentMapper;
import xinhao.foodshare.mapper.FavouriteMapper;
import xinhao.foodshare.mapper.FollowsMapper;
import xinhao.foodshare.mapper.LikesMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.AnnouncementMapper;
import xinhao.foodshare.mapper.ReportMapper;
import xinhao.foodshare.mapper.TagMapper;
import xinhao.foodshare.mapper.PostStateMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.UserRoleMapper;
import xinhao.foodshare.mapper.ViewHistoryMapper;
import xinhao.foodshare.pojo.dto.CommentDTO;
import xinhao.foodshare.pojo.dto.PostDTO;
import xinhao.foodshare.pojo.dto.ReportDTO;
import xinhao.foodshare.pojo.dto.UserRegisterDTO;
import xinhao.foodshare.pojo.dto.UserUpdateDTO;
import xinhao.foodshare.pojo.entity.Announcement;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Favourite;
import xinhao.foodshare.pojo.entity.Follows;
import xinhao.foodshare.pojo.entity.Likes;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.PostState;
import xinhao.foodshare.pojo.entity.Report;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.ViewHistory;
import xinhao.foodshare.pojo.entity.permission.UserRole;
import xinhao.foodshare.pojo.vo.LoginUser;
import xinhao.foodshare.pojo.vo.AnnouncementVO;
import xinhao.foodshare.pojo.vo.FollowsVO;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.service.UserService;
import xinhao.foodshare.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FollowsMapper followsMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ViewHistoryMapper viewHistoryMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Autowired
    private PostUtils postUtils;

    @Autowired
    private PostStateMapper postStateMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LikesMapper likesMapper;

    @Autowired
    private FollowsMapper followMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 举报
     * @param reportDTO 举报信息
     */
    @Override
    public void report(ReportDTO reportDTO) {
        Report report = new Report();
        BeanUtils.copyProperties(reportDTO, report);
        
        // 设置默认值
        report.setReporterId(SecurityUtils.getUserId());
        report.setIsStatus(0); // 0未处理
        report.setCreateTime(LocalDateTime.now());

        reportMapper.insert(report);
    }

    /**
     * 发表评论
     * @param commentDTO 评论信息
     */
    @Override
    @Transactional
    public void addComment(CommentDTO commentDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 1. 插入评论
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.insert(comment);
        
        // 2. 更新帖子评论数
        PostState postState = postStateMapper.selectOne(new LambdaQueryWrapper<PostState>()
                .eq(PostState::getPostId, commentDTO.getPostId()));
        if (postState != null) {
            postState.setCommentCount(postState.getCommentCount() + 1);
            postStateMapper.updateById(postState);
        }
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }
        
        // 1. 删除评论
        commentMapper.deleteById(commentId);
        
        // 2. 更新帖子评论数
        PostState postState = postStateMapper.selectOne(new LambdaQueryWrapper<PostState>()
                .eq(PostState::getPostId, comment.getPostId()));
        if (postState != null) {
            postState.setCommentCount(Math.max(0, postState.getCommentCount() - 1));
            postStateMapper.updateById(postState);
        }
    }

    

    /**
     * 分页查询公告
     * @param page 页码
     * @param pageSize 每页数量
     * @return 公告列表
     */
    @Override
    public PageResult<AnnouncementVO> getAnnouncements(Integer page, Integer pageSize) {
        Page<Announcement> announcementPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        
        // 查询所有人可见的公告
        queryWrapper.eq(Announcement::getVisibleTo, 0)
                .orderByDesc(Announcement::getCreateTime);
        
        announcementMapper.selectPage(announcementPage, queryWrapper);
        
        List<AnnouncementVO> announcementVOList = announcementPage.getRecords().stream().map(announcement -> {
            AnnouncementVO vo = new AnnouncementVO();
            BeanUtils.copyProperties(announcement, vo);
            
            // 填充发布者信息
            User user = userMapper.selectById(announcement.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(announcementVOList, announcementPage.getTotal());
    }


    /**
     * 注册用户
     * 
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @Override
    public UserVO register(UserRegisterDTO userRegisterDTO) {
        // 属性拷贝，将UserDTO转换为User实体类
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

        // 插入用户角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(2L);
        userRoleMapper.insert(userRole);


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
     * 
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

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            loginUser.setUser(user);
            redisCache.setCacheObject("login:" + user.getUserId(), loginUser, (int) JwtUtil.JWT_TTL / 1000, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取用户信息
     * 
     * @param userId 用户ID (可选，如果为null则查询当前登录用户)
     * @return 用户信息
     */
    @Override
    public UserVO info(Long userId) {
        User user;
        if (userId == null) {
            // 获取当前登录用户
            user = SecurityUtils.getUser();
        } else {
            // 根据ID查询用户
            user = userMapper.selectById(userId);
        }

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 转换为VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 查询关注数
        LambdaQueryWrapper<Follows> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(Follows::getUserId, user.getUserId());
        userVO.setFollowCount(followsMapper.selectCount(followWrapper));
        
        // 查询粉丝数
        LambdaQueryWrapper<Follows> fansWrapper = new LambdaQueryWrapper<>();
        fansWrapper.eq(Follows::getFollowedId, user.getUserId());
        userVO.setFansCount(followsMapper.selectCount(fansWrapper));

        return userVO;
    }

    /**
     * 查询关注用户
     * 
     * @return 关注用户列表
     */
    @Override
    public PageResult<FollowsVO> follow(Integer page, Integer pageSize) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();

        // 1. 分页查询关注表
        Page<Follows> followsPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Follows> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follows::getUserId, userId);
        followsMapper.selectPage(followsPage, queryWrapper);

        List<Follows> records = followsPage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), followsPage.getTotal());
        }

        // 2. 提取被关注者的ID列表
        List<Long> followedIds = records.stream()
                .map(Follows::getFollowedId)
                .collect(Collectors.toList());

        // 3. 根据ID列表批量查询用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getUserId, followedIds);
        List<User> userList = userMapper.selectList(userWrapper);

        // 4. 构建Map, Key为被关注者ID, Value为备注名
        Map<Long, String> remarkMap = records.stream()
                .collect(Collectors.toMap(Follows::getFollowedId,
                        follows -> follows.getRemarkName() != null ? follows.getRemarkName() : ""));

        // 5. 转换为VO对象
        List<FollowsVO> followsVOList = userList.stream().map(user -> {
            FollowsVO followsVO = new FollowsVO();
            BeanUtils.copyProperties(user, followsVO);
            // 设置备注名
            followsVO.setRemarkName(remarkMap.get(user.getUserId()));
            return followsVO;
        }).collect(Collectors.toList());

        // 6. 返回分页结果
        return new PageResult<>(followsVOList, followsPage.getTotal());
    }

    /**
     * 查询粉丝用户
     * 
     * @return 粉丝用户列表
     */
    @Override
    public PageResult<FollowsVO> fans(Integer page, Integer pageSize) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();

        // 1. 分页查询关注表 (查找谁关注了我)
        // 查询条件：followed_id = 当前用户ID
        Page<Follows> followsPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Follows> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follows::getFollowedId, userId)
                .orderByDesc(Follows::getCreateTime); // 通常按关注时间倒序
        followsMapper.selectPage(followsPage, queryWrapper);

        List<Follows> records = followsPage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), followsPage.getTotal());
        }

        // 2. 提取粉丝的ID列表 (即 user_id)
        List<Long> fanIds = records.stream()
                .map(Follows::getUserId)
                .collect(Collectors.toList());

        // 3. 根据ID列表批量查询粉丝用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.in(User::getUserId, fanIds);
        List<User> userList = userMapper.selectList(userWrapper);

        // 4. 构建Map, Key为粉丝ID (userId), Value为User对象
        Map<Long, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        // 5. 转换为VO对象
        // 注意：这里需要保持分页的顺序，所以遍历 records
        List<FollowsVO> fansVOList = records.stream().map(follow -> {
            FollowsVO followsVO = new FollowsVO();
            User user = userMap.get(follow.getUserId());
            if (user != null) {
                BeanUtils.copyProperties(user, followsVO);
            }
            // 粉丝列表中通常不显示我对粉丝的备注，或者显示粉丝对我的备注？
            // 这里暂且不设置备注名，或者复用 FollowsVO 结构
            // 如果业务需要显示"我是否关注了该粉丝"（互粉状态），需要额外查询
            return followsVO;
        }).collect(Collectors.toList());

        // 6. 返回分页结果
        return new PageResult<>(fansVOList, followsPage.getTotal());
    }

    /**
     * 查询游览记录
     * 
     * @return 游览记录对应的帖子列表
     */
    @Override
    public PageResult<PostVO> viewHistory(Integer page, Integer pageSize) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();

        // 1. 分页查询游览记录表
        Page<ViewHistory> historyPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<ViewHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ViewHistory::getUserId, userId)
                .orderByDesc(ViewHistory::getViewTime);
        viewHistoryMapper.selectPage(historyPage, queryWrapper);

        List<ViewHistory> records = historyPage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), historyPage.getTotal());
        }

        // 2. 提取帖子ID列表
        List<Long> postIds = records.stream()
                .map(ViewHistory::getPostId)
                .collect(Collectors.toList());
                // 去重

        // 3. 批量查询帖子信息
        List<PostVO> postVOList = getPostsByIds(postIds);

        Set<Long> existingPostIds = postVOList.stream()
                .map(PostVO::getPostId)
                .collect(Collectors.toSet());

        List<Long> deletedPostIds = postIds.stream()
                .filter(postId -> postId != null && !existingPostIds.contains(postId))
                .distinct()
                .collect(Collectors.toList());

        if (!deletedPostIds.isEmpty()) {
            LambdaQueryWrapper<ViewHistory> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(ViewHistory::getUserId, userId)
                    .in(ViewHistory::getPostId, deletedPostIds);
            viewHistoryMapper.delete(deleteWrapper);
        }

        long total = historyPage.getTotal();
        if (!deletedPostIds.isEmpty()) {
            total = viewHistoryMapper.selectCount(new LambdaQueryWrapper<ViewHistory>()
                    .eq(ViewHistory::getUserId, userId));
        }

        // 4. 返回分页结果
        return new PageResult<>(postVOList, total);
    }

    /**
     * 查询收藏帖子
     * 
     * @return 收藏帖子列表
     */
    @Override
    public PageResult<PostVO> favourite(Integer page, Integer pageSize) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();

        // 1. 分页查询收藏表
        Page<Favourite> favouritePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Favourite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favourite::getUserId, userId)
                .orderByDesc(Favourite::getCreateTime);
        favouriteMapper.selectPage(favouritePage, queryWrapper);

        List<Favourite> records = favouritePage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), favouritePage.getTotal());
        }

        // 2. 提取帖子ID列表
        List<Long> postIds = records.stream()
                .map(Favourite::getPostId)
                .collect(Collectors.toList());

        // 3. 批量查询帖子信息
        List<PostVO> postVOList = getPostsByIds(postIds);

        // 4. 返回分页结果
        return new PageResult<>(postVOList, favouritePage.getTotal());
    }

    /**
     * 根据帖子ID列表批量查询帖子信息
     * 
     * @param postIds 帖子ID列表
     * @return 帖子列表
     */
    @Override
    public List<PostVO> getPostsByIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 根据ID列表批量查询帖子信息
        LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
        postWrapper.in(Post::getPostId, postIds)
                .eq(Post::getIsDeleted, 0);
        List<Post> postList = postMapper.selectList(postWrapper);

        if (postList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 调用工具类填充关联信息
        return postUtils.fillPostDetails(postList);
    }

    /**
     * 发布帖子
     * 
     * @param postDTO 帖子数据
     * @return 帖子ID
     */
    @Override
    @Transactional
    public Long publish(PostDTO postDTO) {
        // 1. 校验帖子数据
        if (postDTO == null || postDTO.getTitle() == null || postDTO.getTitle().isEmpty() ||
                postDTO.getContent() == null || postDTO.getContent().isEmpty()) {
            throw new IllegalArgumentException("帖子标题和内容不能为空");
        }

        // 2. 将 DTO 转换为 Entity
        Post post = new Post();
        // 先手动处理需要转换类型的字段
        // 处理图片列表 (List<String> -> JSON String)
        if (postDTO.getImages() != null && !postDTO.getImages().isEmpty()) {
            boolean hasInvalidImageUrl = postDTO.getImages().stream()
                    .anyMatch(url -> url != null && (url.startsWith("blob:") || url.startsWith("data:")));
            if (hasInvalidImageUrl) {
                throw new RuntimeException("图片未上传，请先调用 /file/upload 获取图片URL");
            }
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                post.setImages(objectMapper.writeValueAsString(postDTO.getImages()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片列表格式错误");
            }
        }

        // 处理标签列表 (List<String> -> JSON String)
        if (postDTO.getTags() != null && !postDTO.getTags().isEmpty()) {
            try {
                // 去重
                Set<String> uniqueTags = postDTO.getTags().stream()
                        .filter(tag -> tag != null && !tag.trim().isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toSet());
                
                if (!uniqueTags.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    post.setTag(objectMapper.writeValueAsString(new ArrayList<>(uniqueTags)));
                    
                    // 更新标签使用次数
                    for (String tagName : uniqueTags) {
                        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(Tag::getTagName, tagName);
                        Tag existingTag = tagMapper.selectOne(queryWrapper);
                        
                        if (existingTag != null) {
                            existingTag.setUseCount((existingTag.getUseCount() == null ? 0 : existingTag.getUseCount()) + 1);
                            tagMapper.updateById(existingTag);
                        } else {
                            Tag newTag = new Tag();
                            newTag.setTagName(tagName);
                            newTag.setUseCount(1);
                            newTag.setCreateTime(LocalDateTime.now());
                            tagMapper.insert(newTag);
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("标签列表格式错误");
            }
        }

        
        // 再调用 copyProperties，并忽略 images 和 tags，避免覆盖
        BeanUtils.copyProperties(postDTO, post, "images", "tags");

        // 3. 设置帖子作者为当前登录用户
        post.setUserId(SecurityUtils.getUserId());
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());

        // 设置默认状态为已通过 (为了演示方便，实际项目可设为0待审核)
        post.setStatus(2);

        post.setIsDeleted(0);
        post.setLikeCount(0L);
        post.setCommentCount(0L);
        post.setViewCount(0L);
        post.setFavouriteCount(0L);

        // 4. 插入数据库
        postMapper.insert(post);
        // 5. 初始化帖子统计信息

        PostState postState = new PostState();
        postState.setPostId(post.getPostId());
        postState.setLikeCount(0L);
        postState.setCommentCount(0L);
        postState.setViewCount(0L);
        postState.setFavouriteCount(0L);
        postState.setLastUpdate(LocalDateTime.now());

        postStateMapper.insert(postState);

        return post.getPostId();
    }

    /**
     * 查询用户发布的帖子
     * 
     * @param userId 用户ID
     * @return 帖子列表
     */
    @Override
    public PageResult<PostVO> getPosts(Long userId, Integer page, Integer pageSize) {
        // 1. 分页查询帖子
        Page<Post> postPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreateTime);
        postMapper.selectPage(postPage, queryWrapper);

        List<Post> postList = postPage.getRecords();
        if (postList.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), postPage.getTotal());
        }

        // 2. 填充帖子详情
        List<PostVO> postVOList = postUtils.fillPostDetails(postList);

        // 3. 返回分页结果
        return new PageResult<>(postVOList, postPage.getTotal());
    }

    /**
     * 删除帖子
     * 
     * @param postId 帖子ID
     */
    @Override
    public void delete(Long postId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 3. 校验权限（只有作者可以删除）
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该帖子");
        }

        // 4. 逻辑删除
        post.setIsDeleted(1);
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);
    }

    /**
     * 用户点赞帖子
     * @param postId 帖子ID
     */
    @Override
    public void likePost(Long postId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        // 3. 校验权限（不能点赞自己的帖子）
        if (post.getUserId().equals(userId)) {
            throw new RuntimeException("不能点赞自己的帖子");
        }

        // 5. 新增点赞记录
        Likes likes = new Likes();
        likes.setUserId(userId);
        likes.setTargetType(0L);
        likes.setTargetId(postId);
        likes.setCreateTime(LocalDateTime.now());
        likesMapper.insert(likes);

        // 6. 更新帖子点赞数
        Long postLikeCount = post.getLikeCount();
        post.setLikeCount((postLikeCount == null ? 0L : postLikeCount) + 1);
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);
        // 7. 更新帖子状态
        PostState postState = postStateMapper.selectById(postId);
        if (postState == null) {
            throw new RuntimeException("帖子状态不存在");
        }
        Long postStateLikeCount = postState.getLikeCount();
        postState.setLikeCount((postStateLikeCount == null ? 0L : postStateLikeCount) + 1);
        postState.setLastUpdate(LocalDateTime.now());
        postStateMapper.updateById(postState);
    }

    /**
     * 用户取消点赞帖子
     * @param postId 帖子ID
     */
    @Override
    public void unlikePost(Long postId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 3. 校验权限（不能取消自己的点赞）
        if (post.getUserId().equals(userId)) {
            throw new RuntimeException("不能取消自己的点赞");
        }

        // 4. 删除点赞记录
        likesMapper.delete(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, 0L)
                .eq(Likes::getTargetId, postId));

        // 5. 更新帖子点赞数
        Long postLikeCount = post.getLikeCount();
        post.setLikeCount(Math.max(0L, (postLikeCount == null ? 0L : postLikeCount) - 1));
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);
        // 6. 更新帖子状态
        PostState postState = postStateMapper.selectById(postId);
        if (postState == null) {
            throw new RuntimeException("帖子状态不存在");
        }
        Long postStateLikeCount = postState.getLikeCount();
        postState.setLikeCount(Math.max(0L, (postStateLikeCount == null ? 0L : postStateLikeCount) - 1));
        postState.setLastUpdate(LocalDateTime.now());
        postStateMapper.updateById(postState);
    }

    /**
     * 用户点赞评论
     * @param commentId 评论ID
     */
    @Override
    public void likeComment(Long commentId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        // 3. 校验权限（不能点赞自己的评论）
        if (comment.getUserId().equals(userId)) {
            throw new RuntimeException("不能点赞自己的评论");
        }

        // 5. 新增点赞记录
        Likes likes = new Likes();
        likes.setUserId(userId);
        likes.setTargetType(1L);
        likes.setTargetId(commentId);
        likes.setCreateTime(LocalDateTime.now());
        likesMapper.insert(likes);

        // 6. 更新评论点赞数
        Long commentLikeCount = comment.getLikeCount();
        comment.setLikeCount((commentLikeCount == null ? 0L : commentLikeCount) + 1);
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);
    }

    /**
     * 用户取消点赞评论
     * @param commentId 评论ID
     */
    @Override
    public void unlikeComment(Long commentId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        // 3. 校验权限（不能取消自己的点赞）
        if (comment.getUserId().equals(userId)) {
            throw new RuntimeException("不能取消自己的点赞");
        }

        // 4. 删除点赞记录
        likesMapper.delete(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, 1L)
                .eq(Likes::getTargetId, commentId));

        // 5. 更新评论点赞数
        Long commentLikeCount = comment.getLikeCount();
        comment.setLikeCount(Math.max(0L, (commentLikeCount == null ? 0L : commentLikeCount) - 1));
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(comment);
    }

    /**
     * 用户收藏帖子
     * @param postId 帖子ID
     */
    @Override
    public void favoritePost(Long postId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        // 3. 校验权限（不能收藏自己的帖子）
        if (post.getUserId().equals(userId)) {
            throw new RuntimeException("不能收藏自己的帖子");
        }

        // 4. 新增收藏记录
        Favourite favourite = new Favourite();
        favourite.setUserId(userId);
        favourite.setPostId(postId);
        favourite.setCreateTime(LocalDateTime.now());
        favouriteMapper.insert(favourite);

        // 5. 更新帖子收藏数
        post.setFavouriteCount(post.getFavouriteCount() + 1);
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);

        // 6. 更新帖子状态
        PostState postState = postStateMapper.selectById(postId);
        if (postState == null) {
            throw new RuntimeException("帖子状态不存在");
        }
        postState.setFavouriteCount(postState.getFavouriteCount() + 1);
        postState.setLastUpdate(LocalDateTime.now());
        postStateMapper.updateById(postState);
    }

    /**
     * 用户取消收藏帖子
     * @param postId 帖子ID
     */
    @Override
    public void unFavoritePost(Long postId) {
        // 1. 获取当前登录用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        // 3. 校验权限（不能取消自己的收藏）
        if (post.getUserId().equals(userId)) {
            throw new RuntimeException("不能取消自己的收藏");
        }

        // 4. 删除收藏记录
        favouriteMapper.delete(new LambdaQueryWrapper<Favourite>()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getPostId, postId));

        // 5. 更新帖子收藏数
        post.setFavouriteCount(post.getFavouriteCount() - 1);
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);

        // 6. 更新帖子状态
        PostState postState = postStateMapper.selectById(postId);
        if (postState == null) {
            throw new RuntimeException("帖子状态不存在");
        }
        postState.setFavouriteCount(postState.getFavouriteCount() - 1);
        postState.setLastUpdate(LocalDateTime.now());
        postStateMapper.updateById(postState);
    }

    /**
     * 用户关注用户
     * @param userId 用户ID
     */
    @Override
    public void follow(Long userId) {
        // 1. 获取当前登录用户
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        // 2. 校验权限（不能关注自己）
        if (currentUserId.equals(userId)) {
            throw new RuntimeException("不能关注自己");
        }
        Long followCount = followMapper.selectCount(new LambdaQueryWrapper<Follows>()
                .eq(Follows::getUserId, currentUserId)
                .eq(Follows::getFollowedId, userId));
        if (followCount != null && followCount > 0) {
            throw new RuntimeException("已关注该用户");
        }
        // 3. 新增关注记录
        Follows follows = new Follows();
        follows.setUserId(currentUserId);
        follows.setFollowedId(userId);
        follows.setCreateTime(LocalDateTime.now());
        followMapper.insert(follows);
    }

    /**
     * 用户取消关注用户
     * @param userId 用户ID
     */
    @Override
    public void unfollow(Long userId) {
        // 1. 获取当前登录用户
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        // 2. 校验权限（不能取消关注自己）
        if (currentUserId.equals(userId)) {
            throw new RuntimeException("不能取消关注自己");
        }
        // 3. 删除关注记录
        followMapper.delete(new LambdaQueryWrapper<Follows>()
                .eq(Follows::getUserId, currentUserId)
                .eq(Follows::getFollowedId, userId));
    }
}
