package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tag")
public class Tag implements Serializable {
    private Long id;                  // 标签ID

    private String name;               // 标签名称

    private Long usageCount;        // 被使用次数

    private LocalDateTime createTime;  // 创建时间
}