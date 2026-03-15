package xinhao.foodshare.pojo.dto;

import lombok.Data;

@Data
public class AnnouncementDTO {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 可见范围(0所有人1管理员)
     */
    private Integer visibleTo;
}
