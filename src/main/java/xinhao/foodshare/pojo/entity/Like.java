package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like implements Serializable {

    //点赞记录ID
    private Long likeId;

    //用户ID
    private Long userId;              // 点赞用户ID

    //点赞对象类型(0帖子1评论)
    private Integer tagetType;

    //点赞对象ID
    private Long tagetId;

    //创建时间
    private LocalDateTime createTime;

}