package xinhao.foodshare.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//游览足迹表
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("view_history")

public class ViewHistory implements Serializable {

    //游览记录ID
    @TableId(type = IdType.AUTO)
    private Long viewId;

    //用户ID
    private Long userId;

    //帖子ID
    private Long postId;

    //游览时间
    private LocalDateTime viewTime;

}