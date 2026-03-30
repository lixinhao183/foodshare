package xinhao.foodshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xinhao.foodshare.pojo.entity.Local;

import java.util.List;

/**
 * 位置分类服务类
 */
public interface LocalService extends IService<Local> {
    /**
     * 获取所有位置分类
     */
    List<Local> listAllLocals();

    /**
     * 添加位置分类
     */
    boolean addLocal(Local local);

    /**
     * 更新位置分类
     */
    boolean updateLocal(Local local);

    /**
     * 删除位置分类
     */
    boolean deleteLocal(Integer localId);

    /**
     * 根据ID查询位置分类
     */
    Local getLocalById(Integer localId);
}
