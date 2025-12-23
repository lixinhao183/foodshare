package xinhao.foodshare.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory implements Serializable {

    //搜索历史ID
    private Long historyId;

    //用户ID
    private Long userId;

    //搜索关键字
    private String keyword;

    //最后搜索时间
    private LocalDateTime searchTime;

}