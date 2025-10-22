package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//收藏记录表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("favourite")
public class Favourite implements Serializable {
    private Long id;                  // 收藏记录id

    private Long userId;            // 收藏用户id

    private Long postId;            // 收藏帖子id

    private LocalDateTime createTime; // 收藏时间

}