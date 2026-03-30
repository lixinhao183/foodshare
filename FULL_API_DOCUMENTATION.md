# FoodShare API 文档（按当前代码实现校对）

最后校对时间：2026-03-26

## 0. 通用说明

### 0.1 基础返回结构
所有接口统一返回：

```ts
interface ResponseResult<T = any> {
  code: number; // 200 成功，500 失败（或自定义）
  msg: string;
  data: T;
}
```

分页结构：

```ts
interface PageResult<T> {
  records: T[];
  total: number;
}
```

### 0.2 鉴权与权限（按代码）
- 请求头：`Authorization`
- JWT 过滤器已启用。
- Spring Security HTTP 层：
  - `/user/login`、`/user/register` 仅允许匿名访问（`anonymous()`）。
  - 其他路径在 HTTP 层 `permitAll`，主要依赖方法级权限控制。
- 方法级权限（`@PreAuthorize`）：
  - `UserController`：类级 `hasAuthority('system:user:list')`
  - `AdminController`：类级 `hasAuthority('system:dept:list')`
  - `PostController`、`FileController`：无类级权限注解

> 说明：部分业务逻辑内部会直接读取当前登录用户（`SecurityUtils.getUserId()`），未登录时会抛业务异常。

---

## 1. 用户模块（`/user`）

### 1.1 登录
- `POST /user/login`
- Body: `User`
  - `username`
  - `password`
- 返回：`ResponseResult<Map>`（包含 token 等）
- 权限：匿名

### 1.2 退出登录
- `GET /user/logout`
- 返回：`ResponseResult`

### 1.3 注册
- `POST /user/register`
- Body: `UserRegisterDTO`
- 返回：`ResponseResult<UserVO>`
- 权限：匿名

### 1.4 更新用户信息
- `PUT /user/update`
- Body: `UserUpdateDTO`
- 返回：`ResponseResult`

### 1.5 获取用户信息
- `GET /user/info`
- Query:
  - `userId`（可选）
- 返回：`ResponseResult<UserVO>`

### 1.6 关注列表
- `GET /user/follows`
- Query: `page=1`、`pageSize=10`
- 返回：`ResponseResult<PageResult<FollowsVO>>`

### 1.7 粉丝列表
- `GET /user/fans`
- Query: `page=1`、`pageSize=10`
- 返回：`ResponseResult<PageResult<FollowsVO>>`

### 1.8 浏览记录
- `GET /user/viewhistory`
- Query: `page=1`、`pageSize=10`
- 返回：`ResponseResult<PageResult<PostVO>>`

### 1.9 收藏列表
- `GET /user/favourite`
- Query: `page=1`、`pageSize=10`
- 返回：`ResponseResult<PageResult<PostVO>>`

### 1.10 发布帖子
- `POST /user/post/publish`
- Body: `PostDTO`
- 返回：`ResponseResult<Long>`（postId）

### 1.11 查询用户发布帖子
- `GET /user/post`
- Query:
  - `userId`（必填）
  - `page=1`
  - `pageSize=10`
- 返回：`ResponseResult<PageResult<PostVO>>`

### 1.12 删除帖子
- `DELETE /user/post/{postId}`
- 返回：`ResponseResult`

### 1.13 公告列表
- `GET /user/announcement`
- Query: `page=1`、`pageSize=10`
- 返回：`ResponseResult<PageResult<AnnouncementVO>>`

### 1.14 用户举报
- `POST /user/report`
- Body: `ReportDTO`
  - `targetId`
  - `targetType`（0 帖子，1 用户，2 评论）
  - `reasonText`
- 返回：`ResponseResult`
- 逻辑：后端自动写入 `reporterId`、`isStatus=0`、`createTime`

### 1.15 发表评论
- `POST /user/comment`
- Body: `CommentDTO`
- 返回：`ResponseResult`

### 1.16 删除评论
- `DELETE /user/comment/{commentId}`
- 返回：`ResponseResult`

### 1.17 点赞帖子
- `POST /user/like/{postId}`
- 返回：`ResponseResult`

### 1.18 取消点赞帖子
- `DELETE /user/like/{postId}`
- 返回：`ResponseResult`

### 1.19 点赞评论
- `POST /user/like/comment/{commentId}`
- 返回：`ResponseResult`

### 1.20 取消点赞评论
- `DELETE /user/like/comment/{commentId}`
- 返回：`ResponseResult`

### 1.21 收藏帖子
- `POST /user/favourite/{postId}`
- 返回：`ResponseResult`

### 1.22 取消收藏帖子
- `DELETE /user/favourite/{postId}`
- 返回：`ResponseResult`

### 1.23 关注用户
- `POST /user/follow/{userId}`
- 返回：`ResponseResult`

### 1.24 取消关注用户
- `DELETE /user/follow/{userId}`
- 返回：`ResponseResult`

---

## 2. 帖子模块（`/post`）

### 2.1 帖子列表
- `GET /post/list`
- Query:
  - `page=1`
  - `pageSize=10`
  - `sort=new|hot`（默认 `new`）
  - `local`（可选）
  - `price`（可选，例：`0-20`）
  - `keyword`（可选）
  - `tags`（可选，可多值）
- 返回：`ResponseResult<PageResult<PostVO>>`

### 2.2 帖子详情
- `GET /post/detail`
- Query: `postId`（必填）
- 返回：`ResponseResult<PostVO>`

### 2.3 评论列表
- `GET /post/comment/list`
- Query:
  - `postId`（必填）
  - `page=1`
  - `pageSize=10`
- 返回：`ResponseResult<PageResult<Comment>>`

### 2.4 标签列表
- `GET /post/tag`
- Query: 
  - `page=1`
  - `pageSize=10`
  - `tagName`（可选，模糊查询）
- 返回：`ResponseResult<PageResult<Tag>>`

---

## 3. 文件模块（`/file`）

### 3.1 上传文件
- `POST /file/upload`
- Form-Data:
  - `file: MultipartFile`
- 返回：`ResponseResult<String>`（文件可访问 URL）

---

## 4. 管理员模块（`/admin`）

### 4.1 用户列表
- `GET /admin/user/list`
- Query: `page=1`、`pageSize=10`、`username`（可选）
- 返回：`ResponseResult<PageResult<UserVO>>`

### 4.2 修改用户状态
- `PUT /admin/user/status/{userId}/{status}`
- Path:
  - `userId`
  - `status`（0 启用，1 禁用）
- 返回：`ResponseResult`

### 4.3 帖子列表
- `GET /admin/post/list`
- Query: `page=1`、`pageSize=10`、`title`（可选）、`status`（可选）
- 返回：`ResponseResult<PageResult<PostVO>>`

### 4.4 审核帖子
- `PUT /admin/post/audit/{postId}/{status}`
- Path:
  - `postId`
  - `status`（1 未通过，2 已通过）
- 返回：`ResponseResult`

### 4.5 删除帖子
- `DELETE /admin/post/{postId}`
- 返回：`ResponseResult`

### 4.6 发布公告
- `POST /admin/announcement`
- Body: `AnnouncementDTO`
- 返回：`ResponseResult`

### 4.7 删除公告
- `DELETE /admin/announcement/{id}`
- 返回：`ResponseResult`

### 4.8 举报记录列表
- `GET /admin/report/list`
- Query:
  - `page=1`
  - `pageSize=10`
  - `isStatus`（可选，0 未处理，1 已处理）
- 返回：`ResponseResult<PageResult<ReportVO>>`
- 说明：`isStatus` 非 `0/1` 将报错。

### 4.9 处理举报记录（兼容接口）
- `PUT /admin/report/{id}`
- Path:
  - `id` 举报记录 ID
- Query:
  - `isStatus`（可选，0 未处理，1 已处理）
- 返回：`ResponseResult`
- 行为：
  - 传 `isStatus`：按显式状态更新。
  - 不传 `isStatus`：执行状态切换（0 ↔ 1）。
  - 更新为 `1` 时写入 `handlerAdminId=当前管理员ID`；更新为 `0` 时清空 `handlerAdminId`。

### 4.10 批量新增标签
- `POST /admin/tag`
- Body: `List<Tag>`
- 返回：`ResponseResult`

### 4.11 删除标签
- `DELETE /admin/tag/{id}`
- 返回：`ResponseResult`

### 4.12 获取用户角色信息
- `GET /admin/user/role`
- Query: `userId`
- 返回：`ResponseResult<UserRoleInfoVO>`

### 4.13 更新用户角色
- `PUT /admin/user/role`
- Body: `UserRoleUpdateDTO`
- 返回：`ResponseResult`

---

## 5. 状态与字段语义

- 举报状态 `report.isStatus`：
  - `0` 未处理
  - `1` 已处理
- 举报对象类型 `report.targetType`：
  - `0` 帖子
  - `1` 用户
  - `2` 评论
- 帖子审核状态 `post.status`：
  - `0` 未审核
  - `1` 未通过
  - `2` 已通过
- 用户状态 `user.status`：
  - `0` 启用
  - `1` 禁用

---

## 6. 当前实现差异说明（建议前后端对齐）

- 文档基于 Controller/Service 实际代码整理，不再以历史设计稿为准。
- `POST /file/upload` 未显式标注 `@RequestParam("file")`，但按 Multipart 参数名 `file` 传参可用。
- 权限控制以方法注解为主，若前端遇到 401/403，请确认 token 和权限点（`system:user:list` / `system:dept:list`）。
