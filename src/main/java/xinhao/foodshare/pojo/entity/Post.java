package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("post")
public class Post implements Serializable {

    //帖子ID
    @TableId(type = IdType.AUTO)
    private Long postId;

    //作者ID
    private Long userId;

    //帖子标题
    private String title;

    //帖子文字内容
    private String content;

    //图片JSON数组
    private String images;

    //标签JSON数组
    private String tag;

    //价格
    private Float price;

    //推荐分数
    private Integer recommendScore;

    //位置分类(0校内1校外2外卖)
    private Integer locationType;

    //校内位置
    private String campusArea;

    //审核状态(0未审核1未通过2已通过)
    private Integer status;

    //逻辑删除状态(0,1)
    private Integer isDeleted;

    //发布时间
    private LocalDateTime createTime;

    //修改时间
    private LocalDateTime updateTime;

    //游览次数(冗余字段)
    private Long viewCount;

    //点赞次数(冗余字段)
    private Long likeCount;

    //评论次数(冗余字段)
    private Long commentCount;
    
    //收藏次数(冗余字段)
    private Long favouriteCount;


}
