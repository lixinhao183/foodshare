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
@TableName("favourite")
public class Favourite implements Serializable {


    //用户ID
    private Long userId;

    //帖子ID
    private Long postId;

    //收藏时间
    private LocalDateTime createTime;
}