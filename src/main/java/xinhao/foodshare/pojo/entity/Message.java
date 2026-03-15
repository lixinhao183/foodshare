package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//私信与系统通知表
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message implements Serializable {

    //消息ID
    @TableId
    private Long messageId;

    //发送方ID
    private Long fromUserId;

    //接收方ID
    private Long toUserId;

    //消息内容
    private String content;

    //发送时间
    private LocalDateTime createTime;
}