package xinhao.foodshare.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVO implements Serializable {

    //公告ID
    private Long announcementId;

    //发布者ID
    private Long userId;
    
    //发布者用户名
    private String username;

    //标题
    private String title;

    //内容
    private String content;

    //可见范围(0所有人1管理员)
    private Integer visibleTo;

    //发布时间
    private LocalDateTime createTime;
}
