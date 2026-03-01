package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment implements Serializable {

    //评论ID
    @TableId(type = IdType.AUTO)
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
    @TableField(exist = false)
    private Long likeCount;

    //评论者用户名
    @TableField(exist = false)
    private String username;

    //评论者头像
    @TableField(exist = false)
    private String avatar;

    // 当前用户状态
    @TableField(exist = false)
    private Boolean isLiked;

}