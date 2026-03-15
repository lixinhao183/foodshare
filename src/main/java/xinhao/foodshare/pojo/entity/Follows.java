package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//用户关注表
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("follows")
public class Follows implements Serializable {

    //用户id
    @TableId
    private Long userId;

    //被关注者id
    private Long followedId;

    //备注名
    private String remarkName;

    //关注时间
    private LocalDateTime createTime;

}