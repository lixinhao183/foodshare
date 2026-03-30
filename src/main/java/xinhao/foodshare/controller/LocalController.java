package xinhao.foodshare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xinhao.foodshare.pojo.entity.Local;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.service.LocalService;

import java.util.List;

/**
 * 位置分类管理模块
 */
@RestController
@Slf4j
@RequestMapping("/local")
public class LocalController {

    @Autowired
    private LocalService localService;

    /**
     * 查询所有位置分类
     * @return 位置分类列表
     */
    @GetMapping("/list")
    public ResponseResult<List<Local>> list() {
        log.info("查询所有位置分类");
        return ResponseResult.success(localService.listAllLocals());
    }

    /**
     * 根据ID查询位置分类
     * @param localId 位置ID
     * @return 位置分类
     */
    @GetMapping("/{localId}")
    public ResponseResult<Local> getById(@PathVariable Integer localId) {
        log.info("查询位置分类: localId={}", localId);
        return ResponseResult.success(localService.getLocalById(localId));
    }

    /**
     * 添加位置分类
     * @param local 位置信息
     * @return 结果
     */
    @PostMapping
    public ResponseResult add(@RequestBody Local local) {
        log.info("添加位置分类: {}", local);
        localService.addLocal(local);
        return ResponseResult.success();
    }

    /**
     * 更新位置分类
     * @param local 位置信息
     * @return 结果
     */
    @PutMapping
    public ResponseResult update(@RequestBody Local local) {
        log.info("更新位置分类: {}", local);
        localService.updateLocal(local);
        return ResponseResult.success();
    }

    /**
     * 删除位置分类
     * @param localId 位置ID
     * @return 结果
     */
    @DeleteMapping("/{localId}")
    public ResponseResult delete(@PathVariable Integer localId) {
        log.info("删除位置分类: localId={}", localId);
        localService.deleteLocal(localId);
        return ResponseResult.success();
    }
}
