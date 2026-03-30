package xinhao.foodshare.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xinhao.foodshare.pojo.entity.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVO implements Serializable{
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
    
    // 图片列表 (JSON字符串转List)
    private List<String> images;
    // 标签列表 (JSON字符串转List)
    private List<String> tags;
    
    private Float price;
    private Integer local;
    // 位置分类名称
    private String localName;
    
    // 统计数据
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long favouriteCount;
    
    // 时间
    private LocalDateTime createTime;
    
    //审核状态(0未审核1未通过2已通过)
    private Integer status;
    
    // 当前用户状态 是否点赞
    private Integer isLiked;
    // 是否收藏
    private Integer isFavourite;
    
    // 是否关注作者
    private Integer isFollowed;
    
    // 评论列表
    private List<Comment> comments;
}