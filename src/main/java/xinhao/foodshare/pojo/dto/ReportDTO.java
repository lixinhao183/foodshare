package xinhao.foodshare.pojo.dto;

import lombok.Data;

@Data
public class ReportDTO {
    /**
     * 被举报对象ID
     */
    private Long targetId;

    /**
     * 被举报对象类型(0帖子1用户2评论)
     */
    private Integer targetType;

    /**
     * 举报原因
     */
    private String reasonText;

    /**
     * 举报证据(图片或说明)
     */
    private String evidence;
}
