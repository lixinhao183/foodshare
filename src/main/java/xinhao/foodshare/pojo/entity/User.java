package xinhao.foodshare.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户ID
    @TableId(type = IdType.AUTO)
    private Long userId;

    //用户名
    private String username;

    //密码
    private String password;

    //头像URL
    private String image;

    //简介
    private String bio;

    //性别(0未知1男2女)
    private String gender;

    //手机号
    private String phone;

    //邮箱
    private String email;

    //角色(0超级管理员1管理员2普通用户)
    private Integer role;

    //用户状态(0启用,1禁用)
    private Integer status;

    //最后登录时间
    private LocalDateTime lastLoginTime;

    //注册时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}