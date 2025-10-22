package xinhao.foodshare.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

//游览足迹表
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("browsing_history")
public class BrowsingHistory implements Serializable {
    private Long id;                  // 游览记录ID

    private Long userId;              // 用户ID

    private Long postId;              //帖子ID

    private LocalDateTime createTime; // 游览时间


}