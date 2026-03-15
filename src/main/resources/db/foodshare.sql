/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : foodshare

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 15/03/2026 14:46:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_ops_log
-- ----------------------------
DROP TABLE IF EXISTS `admin_ops_log`;
CREATE TABLE `admin_ops_log`  (
  `log_id` int NOT NULL AUTO_INCREMENT COMMENT '管理员操作日志ID',
  `admin_id` int NOT NULL COMMENT '管理员ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型',
  `action_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型',
  `target_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作对象类型（user/post/tag等）',
  `target_id` int NULL DEFAULT NULL COMMENT '操作对象ID',
  `detail` json NULL COMMENT '操作详情（前后数据）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `admin_id`(`admin_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `announcement_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `user_id` int NOT NULL COMMENT '发布者ID',
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '公告内容',
  `visible_to` tinyint NULL DEFAULT 0 COMMENT '公告可见范围(0所有人1管理员)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`announcement_id`) USING BTREE,
  INDEX `created_by`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '评论作者ID',
  `parent_id` int NOT NULL COMMENT '父评论ID(顶级评论为null或0)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评论内容',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '帖子状态(0显示1隐藏2删除)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `like_count` int(10) UNSIGNED ZEROFILL NULL DEFAULT 0000000000 COMMENT '点赞数(冗余字段)',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表（支持多级回复）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favourite
-- ----------------------------
DROP TABLE IF EXISTS `favourite`;
CREATE TABLE `favourite`  (
  `user_id` int NOT NULL COMMENT '收藏用户ID',
  `post_id` int NOT NULL COMMENT '收藏的帖子ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  INDEX `post_id`(`post_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `feedback_id` int NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` int NOT NULL COMMENT '反馈用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '反馈内容',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '管理员回复',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '反馈状态(0未处理1已处理)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`feedback_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for follows
-- ----------------------------
DROP TABLE IF EXISTS `follows`;
CREATE TABLE `follows`  (
  `user_id` int NOT NULL COMMENT '用户ID',
  `followed_id` int NOT NULL COMMENT '用户的关注ID',
  `remark_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '关注时间',
  INDEX `followee_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户关注关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for likes
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes`  (
  `user_id` int NOT NULL COMMENT '点赞用户ID',
  `target_type` tinyint(1) NULL DEFAULT NULL COMMENT '点赞对象类型(0帖子1评论)',
  `target_id` int NULL DEFAULT NULL COMMENT '点赞对象ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for local
-- ----------------------------
DROP TABLE IF EXISTS `local`;
CREATE TABLE `local`  (
  `local_id` int NOT NULL,
  `local_name` int NULL DEFAULT NULL,
  PRIMARY KEY (`local_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `menu_id` int NOT NULL COMMENT '主键,权限id(不为空)',
  `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限名称',
  `perm_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `status` tinyint NULL DEFAULT NULL COMMENT '是否启用(0可用1不可用)',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `from_user_id` int NOT NULL DEFAULT 0 COMMENT '发送方ID',
  `to_user_id` int NOT NULL COMMENT '接收方ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `to_user_id`(`to_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '私信与系统通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `post_id` int NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '作者ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子文字内容',
  `images` json NULL COMMENT '图片JSON数组',
  `tag` json NULL COMMENT '标签数组',
  `price` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '价格',
  `recommend_score` int NULL DEFAULT NULL COMMENT '推荐指数（星级评分）',
  `local` tinyint(1) NULL DEFAULT NULL COMMENT '位置分类（0校内/1校外/2外卖）',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态(0未审核1未通过2已通过)',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '逻辑删除(0未删除,1已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `view_count` int NULL DEFAULT 0 COMMENT '游览次数(默认0)',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数（冗余字段,便于查询）',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数（冗余字段）',
  `favourite_count` int NULL DEFAULT 0 COMMENT '收藏数（冗余字段）',
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `author_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '美食帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_state
-- ----------------------------
DROP TABLE IF EXISTS `post_state`;
CREATE TABLE `post_state`  (
  `post_id` int NOT NULL COMMENT '帖子ID',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `favourite_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览数',
  `last_update` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后统计更新时间',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `report_id` int NOT NULL AUTO_INCREMENT COMMENT '举报记录ID',
  `reporter_id` int NOT NULL COMMENT '举报人ID',
  `target_id` int NOT NULL COMMENT '举报对象ID',
  `target_type` tinyint(1) NOT NULL COMMENT '举报对象类型(0帖子1用户2评论)',
  `reason_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '举报原因',
  `evidence` json NULL COMMENT '举报证据（图片或说明）',
  `is_status` tinyint(1) NULL DEFAULT 0 COMMENT '处理状态(0未处理1已处理)',
  `handler_admin_id` int NULL DEFAULT NULL COMMENT '处理管理员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `reporter_id`(`reporter_id` ASC) USING BTREE,
  INDEX `handler_admin_id`(`handler_admin_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '举报记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` int NOT NULL COMMENT '主键,角色id(不为空)',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色权限字符',
  `status` tinyint NULL DEFAULT NULL COMMENT '是否启用(0可用1不可用)'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `role_id` int NOT NULL COMMENT '角色id',
  `menu_id` int NULL DEFAULT NULL COMMENT '权限id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for search_history
-- ----------------------------
DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history`  (
  `history_id` int NOT NULL COMMENT '搜索历史id',
  `user_id` int NOT NULL COMMENT '用户id',
  `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '搜索关键字',
  `search_time` datetime NULL DEFAULT NULL COMMENT '最后搜索时间',
  PRIMARY KEY (`history_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '搜索历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `tag_id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `use_count` int NULL DEFAULT 0 COMMENT '被使用次数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像URL',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个性签名/简介',
  `gender` tinyint(1) NULL DEFAULT NULL COMMENT '性别（0未知1男2女）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号（唯一，可选）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱（可选，唯一）',
  `role` tinyint(1) NULL DEFAULT 0 COMMENT '用户角色(0超级管理员1管理员2用户)',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '用户状态（0启用/1禁用）',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` int NOT NULL COMMENT '用户id',
  `role_id` int NULL DEFAULT NULL COMMENT '角色id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for view_history
-- ----------------------------
DROP TABLE IF EXISTS `view_history`;
CREATE TABLE `view_history`  (
  `view_id` int NOT NULL AUTO_INCREMENT COMMENT '游览记录id',
  `user_id` int NOT NULL COMMENT '用户ID',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `view_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`view_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '浏览足迹表（Redis可做缓存）' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
