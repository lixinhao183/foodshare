package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

    //评论ID
    private Long commentId;

    //帖子ID
    private Long postId;

    //作者ID
    private Long userId;

    //父评论ID
    private Long parentId;

    //评论内容
    private String content;

    //创建时间
    private LocalDateTime createTime;

    //修改时间
    private LocalDateTime updateTime;

    //点赞数(冗余字段)
    private Integer likeCount;
}