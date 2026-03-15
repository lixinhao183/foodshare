package xinhao.foodshare.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO implements Serializable {

    //帖子标题
    private String title;

    //帖子文字内容
    private String content;

    //图片列表
    private List<String> images;

    //标签列表
    private List<String> tags;

    //价格
    private Float price;

    //推荐分数
    private Integer recommendScore;

    //位置分类
    private Integer local;

    //发布时间
    private LocalDateTime createTime;

    //修改时间
    private LocalDateTime updateTime;
}
