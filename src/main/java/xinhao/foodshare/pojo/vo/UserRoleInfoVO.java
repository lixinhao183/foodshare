package xinhao.foodshare.pojo.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRoleInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Integer userType;
    private List<Long> assignedRoleIds;
    private List<RoleVO> roleList;
}
