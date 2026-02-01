package xinhao.foodshare.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserUpdateDTO implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名 (如果允许修改)
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
