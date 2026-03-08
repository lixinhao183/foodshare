package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="likes")
public class Likes implements Serializable {

    //用户ID
    private Long userId;              // 点赞用户ID

    //点赞对象类型(0帖子1评论)
    private Integer targetType;

    //点赞对象ID
    private Long targetId;

    //创建时间
    private LocalDateTime createTime;

}