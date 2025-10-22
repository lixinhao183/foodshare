package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//美食帖子表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("post")
public class Post implements Serializable {
    private Long id;                  // 帖子ID

    private Long authorID;            // 作者ID

    private String title;             // 帖子标题

    private String content;             // 帖子文字内容

    private String images;             // 图片JSON数组

    private String taste;             //口味(甜辣咸)

    private Integer recommendScore;   // 推荐分数

    private Float price;               // 价格

    private String locationType;       // 位置类型(校内,校外,外卖)0incampus,1outcampus,2takeout

    private String campusArea;         // 校内位置

    private String visibility;         // 可见范围(0all,1user,2admin)

    private String status;             // 审核状态(unreviewed,rejected,approved)(未审核,审核未通过,已通过)

    private Integer isDeleted;         // 删除状态(0,1)

    private LocalDateTime createTime; // 发布时间

    private LocalDateTime updateTime; // 修改时间

}