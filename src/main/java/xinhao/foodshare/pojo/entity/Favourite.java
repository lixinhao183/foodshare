package xinhao.foodshare.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favourite implements Serializable {

    //收藏ID
    private Long favouriteId;

    //用户ID
    private Long userId;

    //帖子ID
    private Long postId;

    //收藏时间
    private LocalDateTime createTime;
}