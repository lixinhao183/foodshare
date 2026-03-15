package xinhao.foodshare.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//管理员操作日志
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_ops_log")
public class AdminOpsLog implements Serializable {

    //管理员操作日志id
    @TableId
    private Long logId;

    //管理员ID
    private Long adminId;

    //管理员用户名
    private String username;

    //管理员角色
    private String roleName;

    //操作类型
    private String actionType;

    //操作对象类型(1user/2post等)
    private Integer targetType;

    //操作对象ID
    private Long targetId;

    //操作详情
    private String detail;

    //操作时间
    private LocalDateTime createTime;


}