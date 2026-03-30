package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xinhao.foodshare.cache.RedisCache;
import xinhao.foodshare.mapper.AnnouncementMapper;
import xinhao.foodshare.mapper.CommentMapper;
import xinhao.foodshare.mapper.PostMapper;
import xinhao.foodshare.mapper.ReportMapper;
import xinhao.foodshare.mapper.RoleMapper;
import xinhao.foodshare.mapper.TagMapper;
import xinhao.foodshare.mapper.UserMapper;
import xinhao.foodshare.mapper.UserRoleMapper;
import xinhao.foodshare.pojo.dto.AnnouncementDTO;
import xinhao.foodshare.pojo.entity.Announcement;
import xinhao.foodshare.pojo.entity.Comment;
import xinhao.foodshare.pojo.entity.Post;
import xinhao.foodshare.pojo.entity.Report;
import xinhao.foodshare.pojo.entity.Tag;
import xinhao.foodshare.pojo.entity.User;
import xinhao.foodshare.pojo.entity.permission.Role;
import xinhao.foodshare.pojo.entity.permission.UserRole;
import xinhao.foodshare.pojo.vo.PostVO;
import xinhao.foodshare.pojo.vo.ReportVO;
import xinhao.foodshare.pojo.vo.RoleVO;
import xinhao.foodshare.pojo.vo.UserRoleInfoVO;
import xinhao.foodshare.pojo.vo.UserVO;
import xinhao.foodshare.result.PageResult;
import xinhao.foodshare.service.AdminService;
import xinhao.foodshare.utils.PostUtils;
import xinhao.foodshare.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostUtils postUtils;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private ReportMapper reportMapper;
    
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 分页查询举报记录
     * @param page 页码
     * @param pageSize 每页数量
     * @param isStatus 处理状态 (可选)
     * @return 举报记录列表
     */
    @Override
    public PageResult<ReportVO> getReportList(Integer page, Integer pageSize, Integer isStatus) {
        if (isStatus != null && isStatus != 0 && isStatus != 1) {
            throw new RuntimeException("处理状态参数错误");
        }

        Page<Report> reportPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
        
        if (isStatus != null) {
            queryWrapper.eq(Report::getIsStatus, isStatus);
        }
        queryWrapper.orderByDesc(Report::getCreateTime);
        
        reportMapper.selectPage(reportPage, queryWrapper);

        List<Report> records = reportPage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), reportPage.getTotal());
        }

        Map<Long, Post> postMapTemp = new HashMap<>();
        Map<Long, User> userMapTemp = new HashMap<>();
        Map<Long, Comment> commentMapTemp = new HashMap<>();

        Set<Long> postIds = records.stream()
                .filter(report -> report.getTargetType() != null && report.getTargetType() == 0)
                .map(Report::getTargetId)
                .collect(Collectors.toSet());
        if (!postIds.isEmpty()) {
            LambdaQueryWrapper<Post> postWrapper = new LambdaQueryWrapper<>();
            postWrapper.in(Post::getPostId, postIds);
            postMapTemp = postMapper.selectList(postWrapper).stream()
                    .collect(Collectors.toMap(Post::getPostId, post -> post));
        }

        Set<Long> userIds = records.stream()
                .filter(report -> report.getTargetType() != null && report.getTargetType() == 1)
                .map(Report::getTargetId)
                .collect(Collectors.toSet());
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(User::getUserId, userIds);
            userMapTemp = userMapper.selectList(userWrapper).stream()
                    .collect(Collectors.toMap(User::getUserId, user -> user));
        }

        Set<Long> commentIds = records.stream()
                .filter(report -> report.getTargetType() != null && report.getTargetType() == 2)
                .map(Report::getTargetId)
                .collect(Collectors.toSet());
        if (!commentIds.isEmpty()) {
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.in(Comment::getCommentId, commentIds);
            commentMapTemp = commentMapper.selectList(commentWrapper).stream()
                    .collect(Collectors.toMap(Comment::getCommentId, comment -> comment));
        }

        final Map<Long, Post> postMap = postMapTemp;
        final Map<Long, User> userMap = userMapTemp;
        final Map<Long, Comment> commentMap = commentMapTemp;

        // 批量查询举报人信息
        Set<Long> reporterIds = records.stream().map(Report::getReporterId).collect(Collectors.toSet());
        Map<Long, User> reporterMap = new HashMap<>();
        if (!reporterIds.isEmpty()) {
            reporterMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getUserId, reporterIds))
                    .stream()
                    .collect(Collectors.toMap(User::getUserId, user -> user));
        }
        final Map<Long, User> finalReporterMap = reporterMap;

        List<ReportVO> reportVOList = records.stream().map(report -> {
            ReportVO vo = new ReportVO();
            BeanUtils.copyProperties(report, vo);
            
            // 填充举报人姓名
            User reporter = finalReporterMap.get(report.getReporterId());
            if (reporter != null) {
                vo.setReporterUsername(reporter.getUsername());
            }

            if (report.getTargetType() == null) {
                vo.setTargetName("未知举报对象");
                return vo;
            }

            // 填充被举报对象信息
            if (report.getTargetType() == 0) { // 帖子
                Post post = postMap.get(report.getTargetId());
                if (post != null) {
                    vo.setTargetName(post.getTitle());
                    vo.setTargetImage(postUtils.getFirstImage(post.getImages()));
                    vo.setTargetStatus(post.getStatus());
                } else {
                    vo.setTargetName("帖子已删除或不存在");
                }
            } else if (report.getTargetType() == 1) { // 用户
                User user = userMap.get(report.getTargetId());
                if (user != null) {
                    vo.setTargetName(user.getUsername());
                    vo.setTargetImage(user.getImage());
                    vo.setTargetStatus(user.getStatus());
                } else {
                    vo.setTargetName("用户已注销或不存在");
                }
            } else if (report.getTargetType() == 2) { // 评论
                Comment comment = commentMap.get(report.getTargetId());
                if (comment != null) {
                     vo.setTargetName(comment.getContent());
                     vo.setPostId(comment.getPostId());
                     // 评论通常没有封面图，可以不设置或设置默认图
                } else {
                    vo.setTargetName("评论已删除或不存在");
                }
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(reportVOList, reportPage.getTotal());
    }

    /**
     * 处理举报记录
     * @param id 举报记录ID
     * @param isStatus 处理状态 (0未处理, 1已处理)
     */
    @Override
    @Transactional
    public void handleReport(Long id, Integer isStatus) {
        if (isStatus != null && isStatus != 0 && isStatus != 1) {
            throw new RuntimeException("处理状态参数错误");
        }

        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new RuntimeException("举报记录不存在");
        }

        int targetStatus = isStatus == null
                ? (report.getIsStatus() != null && report.getIsStatus() == 0 ? 1 : 0)
                : isStatus;

        syncReportedTargetStatus(report, targetStatus);
        report.setIsStatus(targetStatus);
        report.setUpdateTime(LocalDateTime.now());
        reportMapper.updateById(report);
    }

    /**
     * 删除举报记录
     * @param id 举报记录ID
     */
    @Override
    public void deleteReport(Long id) {
        reportMapper.deleteById(id);
    }

    /**
     * 同步被举报对象状态
     * @param report 举报记录
     * @param targetStatus 目标状态 (0待审核, 1未通过, 2已通过)
     */
    @Transactional
    private void syncReportedTargetStatus(Report report, int targetStatus) {
        if (report.getTargetType() == null || report.getTargetId() == null) {
            return;
        }
        if (report.getTargetType() == 1) {
            User user = userMapper.selectById(report.getTargetId());
            if (user != null) {
                user.setStatus(targetStatus);
                user.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(user);
            }
            return;
        }
        if (report.getTargetType() == 0) {
            Post post = postMapper.selectById(report.getTargetId());
            if (post != null) {
                post.setStatus(targetStatus == 1 ? 1 : 2);
                post.setUpdateTime(LocalDateTime.now());
                postMapper.updateById(post);
            }
        }
    }

    /**
     * 发布公告
     * @param announcementDTO 公告信息
     */
    @Override
    public void publishAnnouncement(AnnouncementDTO announcementDTO) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementDTO, announcement);

        // 设置默认值
        announcement.setUserId(SecurityUtils.getUserId());
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        
        if (announcement.getVisibleTo() == null) {
            announcement.setVisibleTo(0); // 默认所有人可见
        }

        announcementMapper.insert(announcement);
    }

    /**
     * 删除公告
     * @param id 公告ID
     */
    @Override
    public void deleteAnnouncement(Integer id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }

    /**
     * 分页查询用户列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param username 用户名（可选，模糊查询）
     * @return 用户列表
     */
    @Override
    public PageResult<UserVO> getUserList(Integer page, Integer pageSize, String username) {
        Page<User> userPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            queryWrapper.like(User::getUsername, username);
        }
        queryWrapper.orderByDesc(User::getCreateTime);

        userMapper.selectPage(userPage, queryWrapper);

        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(userVOList, userPage.getTotal());
    }

    /**
     * 修改用户状态（封禁/解封）
     * @param userId 用户ID
     * @param status 状态（0启用，1禁用）
     */
    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /**
     * 分页查询帖子列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param title 标题（可选，模糊查询）
     * @param status 状态（可选）
     * @return 帖子列表
     */
    @Override
    public PageResult<PostVO> getPostList(Integer page, Integer pageSize, String title, Integer status) {
        Page<Post> postPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            queryWrapper.like(Post::getTitle, title);
        }
        if (status != null) {
            queryWrapper.eq(Post::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Post::getCreateTime);

        postMapper.selectPage(postPage, queryWrapper);

        List<Post> records = postPage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), postPage.getTotal());
        }

        // 填充关联信息
        List<PostVO> vos = postUtils.fillPostDetails(records);

        return new PageResult<>(vos, postPage.getTotal());
    }


    /**
     * 分页查询评论列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param postId 帖子ID
     * @return 评论列表
     */
    @Override
    public PageResult<Comment> listComments(Integer page, Integer pageSize, Long postId) {
        Page<Comment> commentPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        
        if (postId != null) {
            queryWrapper.eq(Comment::getPostId, postId);
        }
        
        queryWrapper.orderByDesc(Comment::getCreateTime);
        commentMapper.selectPage(commentPage, queryWrapper);

        List<Comment> records = commentPage.getRecords();
        if (records.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), commentPage.getTotal());
        }

        // 填充用户信息
        Set<Long> userIds = records.stream().map(Comment::getUserId).collect(Collectors.toSet());
        if (!userIds.isEmpty()) {
            Map<Long, User> userMap = userMapper.selectList(new LambdaQueryWrapper<User>().in(User::getUserId, userIds))
                    .stream().collect(Collectors.toMap(User::getUserId, u -> u));
            records.forEach(c -> {
                User user = userMap.get(c.getUserId());
                if (user != null) {
                    c.setUsername(user.getUsername());
                    c.setAvatar(user.getImage());
                }
            });
        }

        return new PageResult<>(records, commentPage.getTotal());
    }
    
     /**
     * 删除评论
     * @param commentId 评论ID
     */
    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        commentMapper.deleteById(commentId);
    }

    /**
     * 审核帖子
     * @param postId 帖子ID
     * @param status 状态（1未通过，2已通过）
     */
    @Override
    public void updatePostStatus(Long postId, Integer status) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        post.setStatus(status);
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     */
    @Override
    public void deletePost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        // 删除图片
        postUtils.deletePostImages(post);
        
        postMapper.deleteById(postId);
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getPostId, postId));
    }

    /**
     * 添加标签
     * @param tags 标签列表
     */
    @Override
    @Transactional
    public void addTag(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (Tag tag : tags) {
            LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Tag::getTagName, tag.getTagName());
            if (tagMapper.selectCount(queryWrapper) == 0) {
                tag.setUseCount(0);
                tag.setCreateTime(LocalDateTime.now());
                tagMapper.insert(tag);
            }
        }
    }

    /**
     * 删除标签
     * @param id 标签ID
     */
    @Override
    public void deleteTag(Integer id) {
        tagMapper.deleteById(id);
    }

    /**
     * 获取用户角色信息
     * @param userId 用户ID
     * @return 用户角色信息
     */
    @Override
    public UserRoleInfoVO getUserRoleInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        List<Long> assignedRoleIds = userRoles.stream().map(UserRole::getRoleId).distinct().collect(Collectors.toList());

        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getStatus, "0").orderByAsc(Role::getRoleId);
        List<Role> roles = roleMapper.selectList(roleWrapper);
        List<RoleVO> roleList = roles.stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());

        UserRoleInfoVO vo = new UserRoleInfoVO();
        vo.setUserId(userId);
        vo.setUserType(user.getRole());
        vo.setAssignedRoleIds(assignedRoleIds);
        vo.setRoleList(roleList);
        return vo;
    }

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        List<Long> distinctRoleIds = roleIds == null ? Collections.emptyList()
                : roleIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());

        if (!distinctRoleIds.isEmpty()) {
            LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(Role::getRoleId, distinctRoleIds).eq(Role::getStatus, "0");
            Long count = roleMapper.selectCount(roleWrapper);
            if (count == null || count != distinctRoleIds.size()) {
                throw new RuntimeException("存在无效角色ID");
            }
        }

        //将roleid复制到user
        user.setRole(distinctRoleIds.isEmpty() ? null : distinctRoleIds.get(0).intValue());
        userMapper.updateById(user);

        LambdaQueryWrapper<UserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);



        for (Long roleId : distinctRoleIds) {
            userRoleMapper.insert(new UserRole(userId, roleId));
        }

        redisCache.deleteObject("login:" + userId);
    }
}
