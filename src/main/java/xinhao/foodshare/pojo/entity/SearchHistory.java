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
@TableName("search_history")
public class SearchHistory implements Serializable {

    //搜索历史ID
    @TableId(type = IdType.AUTO)
    private Long historyId;

    //用户ID
    private Long userId;

    //搜索关键字
    private String keyword;

    //最后搜索时间
    private LocalDateTime searchTime;

}