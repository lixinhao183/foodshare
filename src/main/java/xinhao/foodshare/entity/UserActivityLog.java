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
@TableName("user_activity_log")
public class UserActivityLog implements Serializable {
    private Long id;                 // 行为日志ID

    private Long userId;             // 用户ID

    private String actionType;       // 操作类型

    private String targetType;       // 目标类型

    private String targetId;          // 目标ID

    private String metadata;            // 附加信息

    private LocalDateTime createTime;  // 记录时间
}