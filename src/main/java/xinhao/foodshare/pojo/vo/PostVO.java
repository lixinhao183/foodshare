package xinhao.foodshare.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xinhao.foodshare.pojo.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVO{
    // 帖子ID
    private Long postId;
    // 作者ID
    private Long userId;
    // 作者用户名
    private String username;
    // 作者头像
    private String avatar;
    
    // 帖子内容
    private String title;
    private String content;
    private String images;
    private String tags;
    private Float price;
    private Integer recommendScore;
    private Integer locationType;
    private String campusArea;
    
    // 统计数据
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long favouriteCount;
    
    // 时间
    private LocalDateTime createTime;
    
    // 当前用户状态 是否点赞
    private Boolean isLiked;
    // 是否收藏
    private Boolean isFavourite;
    
    // 评论列表
    private List<Comment> comments;
}