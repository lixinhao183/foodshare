package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//管理员操作日志
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("admin_ops_log")
public class AdminOpsLog implements Serializable {
    private Long id;                  // 公告ID

    private Long adminId;             // 管理员ID

    private String actionType;        // 操作类型

    private String targetType;        // 操作对象类型

    private Long targetId;            // 操作对象ID

    private String detail;            // 操作详情

    private LocalDateTime createTime; // 操作时间


}