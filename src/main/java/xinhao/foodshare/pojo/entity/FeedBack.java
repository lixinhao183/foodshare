package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//用户反馈表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBack implements Serializable {

    //反馈ID
    private Long feedbackId;

    //反馈用户ID
    private Long userId;

    //反馈内容
    private String content;

    //管理员回复
    private String reply;

    //状态（0未处理，1已处理）
    private Integer status;

    //提交时间
    private LocalDateTime createTime;

    //回复时间
    private LocalDateTime updateTime;
}