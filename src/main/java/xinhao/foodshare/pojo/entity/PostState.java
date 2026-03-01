package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//帖子统计表
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("post_state")
public class PostState implements Serializable {

    //帖子ID
    @TableId(type = IdType.AUTO)
    private Long postId;

    //点赞数
    private Long likeCount;

    //评论数
    private Long commentCount;

    //收藏数
    private Long favouriteCount;

    //游览数
    private Long viewCount;

    //最后统计更新时间
    private LocalDateTime lastUpdate;

}