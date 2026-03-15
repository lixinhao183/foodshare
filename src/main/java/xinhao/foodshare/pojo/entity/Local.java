package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("local")
public class Local implements Serializable {

    //位置ID
    private Long localId;

    //位置名称
    private String localName;

}