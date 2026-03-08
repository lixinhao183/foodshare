package xinhao.foodshare.pojo.entity.permission;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 权限菜单表(Menu)实体类
 *
 * @author makejava
 * @since 2021-11-24 15:30:08
 */
@TableName(value="menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 权限菜单ID
     */
    @TableId
    private Long menuId;
    /**
     * 权限菜单名
     */
    private String menuName;
    /**
     * 权限标识
     */
    private String permKey;

    /**
     * 权限菜单状态（0正常 1停用）
     */
    private String status;
}

