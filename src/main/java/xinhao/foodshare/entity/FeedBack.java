package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//用户反馈表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("feedback")
public class FeedBack implements Serializable {
    private Long id;                  // 反馈ID

    private Long userId;              // 反馈用户ID

    private String content;           // 反馈内容

    private String reply;             // 管理员回复

    private Integer status;           // 状态（0未处理，1已处理）

    private LocalDateTime createTime; // 提交时间

    private LocalDateTime updateTime; // 回复时间
}