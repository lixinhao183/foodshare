package xinhao.foodshare.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportVO implements Serializable {

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

    //举报时间
    private LocalDateTime createTime;

    // 被举报对象名称 (帖子标题/用户名/评论内容)
    private String targetName;

    // 被举报对象图片 (帖子图片/用户头像)
    private String targetImage;
}
