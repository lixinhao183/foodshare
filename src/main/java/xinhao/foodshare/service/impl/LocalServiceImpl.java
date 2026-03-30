package xinhao.foodshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xinhao.foodshare.mapper.LocalMapper;
import xinhao.foodshare.pojo.entity.Local;
import xinhao.foodshare.service.LocalService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 位置分类服务实现类
 */
@Service
public class LocalServiceImpl extends ServiceImpl<LocalMapper, Local> implements LocalService {

    @Override
    public List<Local> listAllLocals() {
        return list();
    }

    @Override
    public boolean addLocal(Local local) {
        local.setCreateTime(LocalDateTime.now());
        local.setUpdateTime(LocalDateTime.now());
        return save(local);
    }

    @Override
    public boolean updateLocal(Local local) {
        local.setUpdateTime(LocalDateTime.now());
        return updateById(local);
    }

    @Override
    public boolean deleteLocal(Integer localId) {
        return removeById(localId);
    }

    @Override
    public Local getLocalById(Integer localId) {
        return getById(localId);
    }
}
