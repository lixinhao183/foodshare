package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//系统公告表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement implements Serializable {

    //公告ID
    private Long announcementId;

    //发布者ID
    private Long userId;

    //标题
    private String title;

    //内容
    private String content;

    //可见范围(0所有人1管理员)
    private Integer visibleTo;

    //发布时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

}