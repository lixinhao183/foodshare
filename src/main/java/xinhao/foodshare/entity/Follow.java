package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//用户关注关系表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("follow")
public class Follow implements Serializable {
    private Long followerId;                  // 关注者id

    private Long followeeId;            // 被关注者id

    private LocalDateTime createTime; // 关注时间

}