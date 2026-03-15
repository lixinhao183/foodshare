package xinhao.foodshare.pojo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleUpdateDTO {
    private Long userId;
    private List<Long> roleIds;
}
