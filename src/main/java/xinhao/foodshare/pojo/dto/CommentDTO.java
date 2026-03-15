package xinhao.foodshare.pojo.dto;

import lombok.Data;

@Data
public class CommentDTO {
    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 父评论ID (可选，回复评论时使用)
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;
}
