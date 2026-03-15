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
@TableName("tag")
public class Tag implements Serializable {

    //标签ID
    @TableId(type = IdType.AUTO)
    private Long tagId;

    //标签名称
    private String tagName;

    //使用次数
    private Integer useCount;

    //创建时间
    private LocalDateTime createTime;
}