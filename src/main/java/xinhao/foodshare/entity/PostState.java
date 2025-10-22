package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//帖子统计表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("post_state")
public class PostState implements Serializable {
    private Long id;                  // 帖子ID

    private Long likeCount;            // 点赞数

    private Long commentCount;         // 评论数

    private Long favouriteCount;       // 收藏数

    private Long viewCount;            // 游览数

    private LocalDateTime updateTime;  // 最后统计更新时间

}