package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

//用户关注表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follows implements Serializable {

    //主键,关注记录id
    private Long followId;

    //关注者id
    private Long followerId;

    //被关注者id
    private Long followedId;

    //备注名
    private String remarkName;

    //关注时间
    private LocalDateTime createTime;

}