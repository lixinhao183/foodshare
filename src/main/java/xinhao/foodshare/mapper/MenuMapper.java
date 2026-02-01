package xinhao.foodshare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xinhao.foodshare.pojo.entity.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu>{

    List<String> selectPermKeysByUserId(Long userId);
}
