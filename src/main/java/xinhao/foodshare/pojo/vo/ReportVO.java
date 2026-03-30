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

    //举报人用户名
    private String reporterUsername;

    //举报原因
    private String reasonText;

    //处理状态(0未处理1已处理)
    private Integer isStatus;

    //举报时间
    private LocalDateTime createTime;

    // 被举报对象名称 (帖子标题/用户名/评论内容)
    private String targetName;

    // 被举报对象图片 (帖子图片/用户头像)
    private String targetImage;

    // 帖子ID (举报对象为评论时使用)
    private Long postId;

    /**
     * 被举报对象状态 (如果是用户, 返回用户状态 0启用 1禁用; 如果是帖子, 返回帖子状态 0未审核 1未通过 2已通过)
     */
    private Integer targetStatus;
}
