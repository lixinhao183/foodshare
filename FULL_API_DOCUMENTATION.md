# FoodShare API 文档

## 0. 通用说明 (Common)

### 0.1 认证与请求头
*   **Token Header**: `Authorization`
    *   支持两种格式：`Bearer <token>` 或直接 `<token>`
*   **说明**:
    *   `/user/login`、`/user/register` 可匿名访问
    *   其余接口中，部分接口在业务实现里会依赖“当前登录用户”（例如调用 `SecurityUtils.getUserId()`），未登录可能返回 500（当前安全配置未对这些接口做强制拦截）

## 1. 用户模块 (User Module)
Base URL: `/user`

### 1.1 用户登录
*   **接口说明**: 用户登录接口
*   **请求方式**: `POST`
*   **接口地址**: `/user/login`
*   **请求参数**:
    *   Body: `User` (JSON)
        *   `username`: 用户名
        *   `password`: 密码
*   **返回数据**: `ResponseResult<Map>`
    *   包含 JWT token 等信息

### 1.2 用户退出登录
*   **接口说明**: 用户退出登录
*   **请求方式**: `GET`
*   **接口地址**: `/user/logout`
*   **请求参数**: 无
*   **返回数据**: `ResponseResult<String>`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.3 用户注册
*   **接口说明**: 用户注册
*   **请求方式**: `POST`
*   **接口地址**: `/user/register`
*   **请求参数**:
    *   Body: `UserRegisterDTO` (JSON)
        *   `username`: 用户名
        *   `password`: 密码
        *   `image`: 头像URL
        *   `bio`: 简介
        *   `gender`: 性别
        *   `phone`: 手机号
        *   `email`: 邮箱
        *   `status`: 状态
*   **返回数据**: `ResponseResult<UserVO>`

### 1.4 更新用户信息
*   **接口说明**: 更新当前用户信息（包括头像）
*   **请求方式**: `PUT`
*   **接口地址**: `/user/update`
*   **请求参数**:
    *   Body: `UserUpdateDTO` (JSON)
        *   `image`: 头像URL (通过文件上传接口获取)
        *   `bio`: 简介
        *   `gender`: 性别
        *   ...
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.5 获取个人信息
*   **接口说明**: 获取用户个人信息（可查指定用户，或查当前登录用户）
*   **请求方式**: `GET`
*   **接口地址**: `/user/info`
*   **请求参数**:
    *   `userId`: 用户ID (可选，不传则返回当前登录用户信息)
*   **返回数据**: `ResponseResult<UserVO>`
*   **鉴权**:
    *   `userId` 不传：需要登录（业务实现依赖当前用户）
    *   `userId` 传入：不强制登录

### 1.6 查询关注用户
*   **接口说明**: 分页查询当前用户的关注列表
*   **请求方式**: `GET`
*   **接口地址**: `/user/follows`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<FollowsVO>>`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.7 查询粉丝用户
*   **接口说明**: 分页查询当前用户的粉丝列表
*   **请求方式**: `GET`
*   **接口地址**: `/user/fans`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<FollowsVO>>`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.8 查询浏览记录
*   **接口说明**: 分页查询当前用户的浏览记录
*   **请求方式**: `GET`
*   **接口地址**: `/user/viewhistory`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<PostVO>>`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.9 查询收藏帖子
*   **接口说明**: 分页查询当前用户收藏的帖子
*   **请求方式**: `GET`
*   **接口地址**: `/user/favourite`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<PostVO>>`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.10 发布帖子
*   **接口说明**: 用户发布新帖子
*   **请求方式**: `POST`
*   **接口地址**: `/user/post/publish`
*   **请求参数**:
    *   Body: `PostDTO` (JSON)
        *   `title`: 标题
        *   `content`: 内容
        *   `images`: 图片URL列表 (`List<String>`)
        *   `tags`: 标签列表 (`List<String>`)
        *   `price`: 价格
        *   `local`: 位置
*   **返回数据**: `ResponseResult<Long>`
    *   返回新发布的帖子ID
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.11 查询用户发布的帖子
*   **接口说明**: 分页查询指定用户发布的帖子列表
*   **请求方式**: `GET`
*   **接口地址**: `/user/post`
*   **请求参数**:
    *   `userId`: 用户ID (必填)
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<PostVO>>`

### 1.12 删除帖子
*   **接口说明**: 用户删除自己发布的帖子
*   **请求方式**: `DELETE`
*   **接口地址**: `/user/post/{postId}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户，且通常仅作者可删）

### 1.13 查询公告列表
*   **接口说明**: 分页查询系统公告
*   **请求方式**: `GET`
*   **接口地址**: `/user/announcement`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<AnnouncementVO>>`

### 1.14 用户举报
*   **接口说明**: 用户提交举报信息
*   **请求方式**: `POST`
*   **接口地址**: `/user/report`
*   **请求参数**:
    *   Body: `ReportDTO` (JSON)
        *   `targetId`: 被举报对象ID
        *   `targetType`: 对象类型(0帖子, 1用户, 2评论)
        *   `reasonText`: 原因
        *   `evidence`: 证据
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.15 发表评论
*   **接口说明**: 用户发表评论
*   **请求方式**: `POST`
*   **接口地址**: `/user/comment`
*   **请求参数**:
    *   Body: `CommentDTO` (JSON)
        *   `postId`: 帖子ID
        *   `parentId`: 父评论ID (可选，用于回复)
        *   `content`: 评论内容
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.16 删除评论
*   **接口说明**: 用户删除自己的评论
*   **请求方式**: `DELETE`
*   **接口地址**: `/user/comment/{commentId}`
*   **请求参数**:
    *   `commentId`: 评论ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户，且通常仅本人可删）

### 1.17 关注用户
*   **接口说明**: 用户关注其他用户
*   **请求方式**: `POST`
*   **接口地址**: `/user/follow/{userId}`
*   **请求参数**:
    *   `userId`: 被关注用户ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.18 取消关注用户
*   **接口说明**: 用户取消关注其他用户
*   **请求方式**: `DELETE`
*   **接口地址**: `/user/follow/{userId}`
*   **请求参数**:
    *   `userId`: 被取消关注用户ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.19 点赞帖子
*   **接口说明**: 用户点赞帖子
*   **请求方式**: `POST`
*   **接口地址**: `/user/like/{postId}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.20 取消点赞帖子
*   **接口说明**: 用户取消点赞帖子
*   **请求方式**: `DELETE`
*   **接口地址**: `/user/like/{postId}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.21 点赞评论
*   **接口说明**: 用户点赞评论
*   **请求方式**: `POST`
*   **接口地址**: `/user/like/comment/{commentId}`
*   **请求参数**:
    *   `commentId`: 评论ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.22 取消点赞评论
*   **接口说明**: 用户取消点赞评论
*   **请求方式**: `DELETE`
*   **接口地址**: `/user/like/comment/{commentId}`
*   **请求参数**:
    *   `commentId`: 评论ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.23 用户收藏帖子
*   **接口说明**: 用户收藏帖子
*   **请求方式**: `POST`
*   **接口地址**: `/user/favourite/{postId}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）

### 1.24 用户取消收藏帖子
*   **接口说明**: 用户取消收藏帖子
*   **请求方式**: `DELETE`
*   **接口地址**: `/user/favourite/{postId}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（业务实现依赖当前用户）
---

## 2. 帖子模块 (Post Module)
Base URL: `/post`

### 2.1 帖子列表 (首页/发现)
*   **接口说明**: 分页查询帖子列表，支持排序、筛选
*   **请求方式**: `GET`
*   **接口地址**: `/post/list`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
    *   `sort`: 排序方式 (默认"new", 可选"hot")
    *   `local`: 地点筛选 (可选)
    *   `price`: 价格范围 (可选, 形如 "0-10")
    *   `keyword`: 关键词搜索 (可选)
    *   `tags`: 标签列表 (可选, List<String>)
*   **返回数据**: `ResponseResult<PageResult<PostVO>>`

### 2.2 帖子详情
*   **接口说明**: 查询帖子详细信息
*   **请求方式**: `GET`
*   **接口地址**: `/post/detail`
*   **请求参数**:
    *   `postId`: 帖子ID (必填)
*   **返回数据**: `ResponseResult<PostVO>`
*   

### 2.3 查询评论列表
*   **接口说明**: 分页查询指定帖子的评论
*   **请求方式**: `GET`
*   **接口地址**: `/post/comment/list`
*   **请求参数**:
    *   `postId`: 帖子ID (必填)
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<Comment>>`

### 2.4 查询标签列表
*   **接口说明**: 分页查询标签列表
*   **请求方式**: `GET`
*   **接口地址**: `/post/tag`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
*   **返回数据**: `ResponseResult<PageResult<Tag>>`

---

## 3. 文件模块 (File Module)
Base URL: `/file`

### 3.1 文件上传
*   **接口说明**: 上传图片文件
*   **请求方式**: `POST`
*   **接口地址**: `/file/upload`
*   **请求参数**:
    *   `file`: 文件对象 (MultipartFile)
*   **返回数据**: `ResponseResult<String>`
    *   返回完整的图片访问URL (如 `http://localhost:8080/uploads/xxx.jpg`)
*   **鉴权**: 需要登录（业务实现依赖当前用户）

---

## 4. 管理员模块 (Admin Module)
Base URL: `/admin`

### 4.1 用户列表
*   **接口说明**: 分页查询系统用户列表
*   **请求方式**: `GET`
*   **接口地址**: `/admin/user/list`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
    *   `username`: 用户名模糊搜索 (可选)
*   **返回数据**: `ResponseResult<PageResult<UserVO>>`
*   **鉴权**: 需要登录（角色：admin）

### 4.2 修改用户状态
*   **接口说明**: 封禁或解封用户
*   **请求方式**: `PUT`
*   **接口地址**: `/admin/user/status/{userId}/{status}`
*   **请求参数**:
    *   `userId`: 用户ID (Path Variable)
    *   `status`: 状态 (Path Variable, 0启用, 1禁用)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）

### 4.3 帖子列表
*   **接口说明**: 分页查询系统帖子列表
*   **请求方式**: `GET`
*   **接口地址**: `/admin/post/list`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
    *   `title`: 标题模糊搜索 (可选)
    *   `status`: 状态筛选 (可选)
*   **返回数据**: `ResponseResult<PageResult<PostVO>>`
*   **鉴权**: 需要登录（角色：admin）

### 4.4 审核帖子
*   **接口说明**: 审核帖子通过或拒绝
*   **请求方式**: `PUT`
*   **接口地址**: `/admin/post/audit/{postId}/{status}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
    *   `status`: 审核状态 (Path Variable, 1未通过, 2已通过)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）

### 4.5 删除帖子
*   **接口说明**: 管理员强制删除帖子
*   **请求方式**: `DELETE`
*   **接口地址**: `/admin/post/{postId}`
*   **请求参数**:
    *   `postId`: 帖子ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）

### 4.6 发布公告
*   **接口说明**: 管理员发布系统公告
*   **请求方式**: `POST`
*   **接口地址**: `/admin/announcement`
*   **请求参数**:
    *   Body: `AnnouncementDTO` (JSON)
        *   `title`: 标题
        *   `content`: 内容
        *   `visibleTo`: 可见范围 (0所有人, 1管理员)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）  

### 4.7 删除公告
*   **接口说明**: 管理员删除系统公告
*   **请求方式**: `DELETE`
*   **接口地址**: `/admin/announcement/{id}`
*   **请求参数**:
    *   `id`: 公告ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）  

### 4.8 处理举报记录
*   **接口说明**: 管理员处理用户举报记录（标记为已处理或未处理）
*   **请求方式**: `PUT`
*   **接口地址**: `/admin/report/{id}`
*   **请求参数**:
    *   `id`: 举报记录ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）  



### 4.8 举报记录列表
*   **接口说明**: 分页查询用户举报记录
*   **请求方式**: `GET`
*   **接口地址**: `/admin/report/list`
*   **请求参数**:
    *   `page`: 页码 (默认1)
    *   `pageSize`: 每页数量 (默认10)
    *   `isStatus`: 处理状态筛选 (可选, 0未处理, 1已处理)
*   **返回数据**: `ResponseResult<PageResult<ReportVO>>`
*   **鉴权**: 需要登录（角色：admin）

### 4.9 批量新增标签
*   **接口说明**: 管理员批量新增标签
*   **请求方式**: `POST`
*   **接口地址**: `/admin/tag`
*   **请求参数**:
    *   Body: `List<Tag>` (JSON)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）

### 4.10 删除标签
*   **接口说明**: 管理员删除标签
*   **请求方式**: `DELETE`
*   **接口地址**: `/admin/tag/{id}`
*   **请求参数**:
    *   `id`: 标签ID (Path Variable)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）

### 4.11 查看用户角色
*   **接口说明**: 查看指定用户的角色信息（user_role）与可选角色列表（role）
*   **请求方式**: `GET`
*   **接口地址**: `/admin/user/role`
*   **请求参数**:
    *   `userId`: 用户ID (必填)
*   **返回数据**: `ResponseResult<UserRoleInfoVO>`
*   **鉴权**: 需要登录（角色：admin）

### 4.12 修改用户角色
*   **接口说明**: 覆盖式更新指定用户的角色（先清空 user_role，再按 roleIds 写入）
*   **请求方式**: `PUT`
*   **接口地址**: `/admin/user/role`
*   **请求参数**:
    *   Body: `UserRoleUpdateDTO` (JSON)
        *   `userId`: 用户ID
        *   `roleIds`: 角色ID列表 (可为空数组，表示清空角色)
*   **返回数据**: `ResponseResult`
*   **鉴权**: 需要登录（角色：admin）
*   **备注**: 角色变更后会清理该用户登录态，需要重新登录生效


## 5. 数据模型 (Data Models)

### 5.1 基础响应结构 (Common Response)
所有接口统一返回该结构：
```typescript
interface ResponseResult<T = any> {
  code: number;      // 状态码，200表示成功，其他为失败
  msg: string;       // 提示信息
  data: T;           // 具体数据
}
```

### 5.2 分页数据结构 (Page Result)
```typescript
interface PageResult<T> {
  records: T[];      // 数据列表
  total: number;     // 总记录数
}
```

### 5.3 用户实体 (User / UserVO)
```typescript
interface UserVO {
  userId: number;    // 用户ID
  username: string;  // 用户名
  image: string;     // 头像URL
  bio: string;       // 简介
  gender: string;    // 性别 (0未知, 1男, 2女)
  phone: string;     // 手机号
  email: string;     // 邮箱
}

interface UserUpdateDTO {
  userId: number;    // 用户ID
  username?: string; // 用户名
  image?: string;    // 头像URL
  bio?: string;      // 简介
  gender?: string;   // 性别
  phone?: string;    // 手机号
  email?: string;    // 邮箱
}
```

### 5.4 帖子实体 (Post / PostVO)
```typescript
interface PostVO {
  postId: number;           // 帖子ID
  userId: number;           // 作者ID
  username: string;         // 作者用户名
  avatar: string;           // 作者头像
  
  title: string;            // 标题
  content: string;          // 内容
  images: string[];         // 图片URL列表
  tags: string[];           // 标签列表
  price: number;            // 价格
  recommendScore: number;   // 推荐分数
  local: number;            // 位置分类 (0校内, 1校外, 2外卖)
  
  viewCount: number;        // 浏览数
  likeCount: number;        // 点赞数
  commentCount: number;     // 评论数
  favouriteCount: number;   // 收藏数
  
  createTime: string;       // 发布时间 (ISO 8601)
  
  isLiked: number;          // 当前用户是否点赞 (0否, 1是)
  isFavourite: number;      // 当前用户是否收藏 (0否, 1是)
  
  comments: Comment[];      // 评论列表
}

interface PostDTO {
  title: string;            // 标题
  content: string;          // 内容
  images: string[];         // 图片URL列表
  tags: string[];           // 标签列表
  price?: number;           // 价格
  recommendScore?: number;  // 推荐分数
  local?: number;           // 位置分类
  createTime?: string;      // 发布时间 (ISO 8601)
  updateTime?: string;      // 修改时间 (ISO 8601)
}
```

### 5.5 评论实体 (Comment / CommentDTO)
```typescript
interface Comment {
  commentId: number;        // 评论ID
  postId: number;           // 帖子ID
  userId: number;           // 评论者ID
  parentId: number;         // 父评论ID
  content: string;          // 内容
  createTime: string;       // 时间
  username: string;         // 评论者用户名
  avatar: string;           // 评论者头像
  likeCount: number;        // 点赞数
  isLiked: number;          // 当前用户是否点赞
}

interface CommentDTO {
  postId: number;           // 帖子ID
  parentId?: number;        // 父评论ID (可选)
  content: string;          // 内容
}
```

### 5.7 标签实体 (Tag)
```typescript
interface Tag {
  tagId: number;       // 标签ID
  tagName: string;     // 标签名称
  useCount: number;    // 使用次数
  createTime: string;  // 创建时间 (ISO 8601)
}
```

### 5.8 权限点实体 (Menu)
```typescript
interface Menu {
  menuId: number;      // 权限点ID
  menuName: string;    // 权限名称
  permKey: string;     // 权限标识
  status: string;      // 状态("0"正常, "1"停用)
}
```

### 5.9 角色实体 (Role)
```typescript
interface Role {
  roleId: number;      // 角色ID
  roleName: string;    // 角色名称
  roleKey: string;     // 角色标识
  status: string;      // 状态("0"正常, "1"停用)
}
```

### 5.10 角色 VO (RoleVO)
```typescript
interface RoleVO {
  roleId: number;      // 角色ID
  roleName: string;    // 角色名称
  roleKey: string;     // 角色标识
  status: string;      // 状态("0"正常, "1"停用)
}
```

### 5.11 用户角色信息 (UserRoleInfoVO)
```typescript
interface UserRoleInfoVO {
  userId: number;             // 用户ID
  userType: number;           // 用户类型(user.role: 0超管,1管理员,2普通用户)
  assignedRoleIds: number[];  // 已分配角色ID列表
  roleList: RoleVO[];         // 可选角色列表
}
```

### 5.12 用户角色更新 (UserRoleUpdateDTO)
```typescript
interface UserRoleUpdateDTO {
  userId: number;      // 用户ID
  roleIds: number[];   // 角色ID列表
}
```

### 5.6 其他枚举与常量
*   **性别 (Gender)**: `"0"` (未知), `"1"` (男), `"2"` (女)
*   **位置分类 (Local)**: `0` (校内), `1` (校外), `2` (外卖)
*   **审核状态 (Post Status)**: `0` (未审核), `1` (未通过), `2` (已通过)
*   **用户状态 (User Status)**: `0` (启用), `1` (禁用)
*   **举报状态 (Report Status)**: `0` (未处理), `1` (已处理)
