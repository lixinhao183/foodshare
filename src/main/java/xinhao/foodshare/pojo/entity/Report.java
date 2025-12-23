package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//举报记录表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report implements Serializable {

    //举报ID
    private Long reportId;

    //举报人ID
    private Long reporterId;

    // 被举报对象ID
    private Long targetId;

    //被举报对象类型(0帖子1用户2评论)
    private Integer targetType;

    //举报原因
    private String reasonText;

    //举报证据(图片或说明)
    private String evidence;

    //处理状态(0未处理1已处理)
    private Integer isStatus;

    //处理人管理员ID
    private Long handlerAdminId;

    //举报时间
    private LocalDateTime createTime;

    //处理时间
    private LocalDateTime updateTime;



}