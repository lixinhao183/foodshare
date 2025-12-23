package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag implements Serializable {

    //标签ID
    private Long tagId;

    //标签名称
    private String name;

    //使用次数
    private Long usageCount;

    //创建时间
    private LocalDateTime createTime;
}