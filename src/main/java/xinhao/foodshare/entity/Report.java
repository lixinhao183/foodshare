package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//举报记录表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("report")
public class Report implements Serializable {
    private Long id;                  // 举报ID

    private Long reporterId;          // 举报人ID

    private String targetType;        // 举报对象类型(post,user,comment)

    private Long targetId;            // 被举报对象ID

    private String reasonText;            // 举报原因

    private String evidence;          // 举报证据(图片或说明)

    private Integer isStatus;           // 处理状态（0未处理，1已处理）

    private Long handlerAdminId;       // 处理人管理员ID

    private LocalDateTime updateTime;  // 处理时间

    private LocalDateTime createTime; // 举报时间
}