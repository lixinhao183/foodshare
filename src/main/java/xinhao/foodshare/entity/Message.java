package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//私信与系统通知表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("message")
public class Message implements Serializable {
    private Long id;                  // 消息ID

    private Long fromUserId;          // 发送方ID

    private Long toUserId;            // 接收方ID

    private Long conversationId;      // 会话ID(私信分组)

    private String content;           // 消息内容

    private Integer isRead;           // 是否已读

    private LocalDateTime createTime; // 发送时间
}