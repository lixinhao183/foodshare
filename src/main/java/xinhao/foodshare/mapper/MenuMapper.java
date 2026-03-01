package xinhao.foodshare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xinhao.foodshare.pojo.entity.Menu;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单映射器
 * @author lixinhao
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu>{

    List<String> selectPermKeysByUserId(Long userId);
}
