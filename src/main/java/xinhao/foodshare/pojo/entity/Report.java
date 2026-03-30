package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//举报记录表
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("report")
public class Report implements Serializable {

    //举报ID
    @TableId(type = IdType.AUTO)
    private Long reportId;

    //举报人ID
    private Long reporterId;

    // 被举报对象ID
    private Long targetId;

    //被举报对象类型(0帖子1用户2评论)
    private Integer targetType;

    //举报原因
    private String reasonText;

    //处理状态(0未处理1已处理)
    private Integer isStatus;

    //举报时间
    private LocalDateTime createTime;

    //处理时间
    @TableField(exist = false)
    private LocalDateTime updateTime;
}