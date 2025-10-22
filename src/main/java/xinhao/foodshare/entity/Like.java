package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//点赞记录表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("like")
public class Like implements Serializable {
    private Long id;                  // 点赞记录ID

    private Long userId;              // 点赞用户ID

    private String tagetType;         // 点赞对象类型(post,comment)

    private Long tagetId;             // 点赞对象ID

    private LocalDateTime createTime; // 点赞时间

}