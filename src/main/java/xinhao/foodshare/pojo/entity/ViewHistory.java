package xinhao.foodshare.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//游览足迹表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewHistory implements Serializable {

    //游览记录ID
    private Long viewId;

    //用户ID
    private Long userId;

    //帖子ID
    private Long postId;

    //游览时间
    private LocalDateTime createTime;

}