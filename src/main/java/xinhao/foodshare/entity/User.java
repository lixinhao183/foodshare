package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {
    private Long id;                  // 用户ID

    private String username;          // 用户名

    private String password;          // 密码（加密存储）

    private String email;             // 邮箱

    private String phone;             // 手机号

    private String image;            // 头像URL

    private String bio;               // 简介

    private Integer role;             // 角色（0普通用户，1管理员,2超级管理员）

    private Integer status;           // 用户状态(0启用,1禁用)

    private LocalDateTime createTime; // 注册时间

    private LocalDateTime updateTime; // 更新时间

    private LocalDateTime lastLoginTime;  // 最后登录时间
}