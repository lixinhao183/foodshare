package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("likes")
public class Likes implements Serializable {

    //用户ID
    @TableId(type = IdType.INPUT)
    private Long userId;              // 点赞用户ID

    // 点赞ID
    private Long targetType;          // 点赞目标类型（0：帖子，1：评论）

    // 点赞目标ID（帖子ID或评论ID）
    private Long targetId;            // 点赞目标ID（帖子ID或评论ID）

    //创建时间
    private LocalDateTime createTime;

}