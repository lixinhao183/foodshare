package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//评论表,支持多级回复
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {
    private Long id;                  // 评论ID

    private Long postId;          // 帖子ID

    private Long authorId;            // 评论作者ID

    private Long parentCommentId;      // 父评论ID

    private String content;          // 评论内容

    private Integer isDeleted;           // 是否删除

    private LocalDateTime createTime;  // 创建时间
}