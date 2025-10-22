package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//帖子与标签关系
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("like")
public class PostTag implements Serializable {
    private Long postID;                  // 帖子ID

    private Long tagID;                   // 标签ID
}