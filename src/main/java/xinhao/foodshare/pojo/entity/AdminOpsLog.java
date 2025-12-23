package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//管理员操作日志
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOpsLog implements Serializable {

    //管理员操作日志id
    private Long logId;

    //管理员ID
    private Long adminId;

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