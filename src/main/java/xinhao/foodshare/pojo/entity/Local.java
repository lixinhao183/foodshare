package xinhao.foodshare.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 位置分类表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("local")
public class Local implements Serializable {

    //位置ID
    @TableId(type = IdType.AUTO)
    private Integer localId;

    //位置名称
    private String localName;

    //创建时间
    private LocalDateTime createTime;

    //修改时间
    private LocalDateTime updateTime;

}
