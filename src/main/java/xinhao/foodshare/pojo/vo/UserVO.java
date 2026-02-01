package xinhao.foodshare.pojo.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 头像
     */
    private String image;
    /**
     * 简介
     */
    private String bio;
    /**
     * 性别
     */
    private String gender;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;

}
