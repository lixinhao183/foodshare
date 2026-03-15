package xinhao.foodshare.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowsVO implements Serializable{

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 备注名
     */
    private String remarkName;

    /**
     * 头像
     */
    private String image;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
