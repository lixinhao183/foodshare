package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//系统公告表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("announcement")
public class Announcement implements Serializable {
    private Long id;                  // 公告ID

    private String title;             // 公告标题

    private String content;           // 公告内容

    private String visibleTo;         // 可见范围（all,user,admin）

    private Long createId;            // 发布管理员ID

    private LocalDateTime createTime; // 发布时间

    private LocalDateTime updateTime; // 更新时间

}