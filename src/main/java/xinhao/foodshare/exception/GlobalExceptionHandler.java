package xinhao.foodshare.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xinhao.foodshare.result.ResponseResult;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常 (RuntimeException)
     * 只要代码中 throw new RuntimeException("错误信息")，就会进入这里
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseResult systemExceptionHandler(RuntimeException e) {
        log.error("业务异常：{}", e.getMessage());
        return ResponseResult.error(e.getMessage());
    }

    /**
     * 捕获其他未知异常 (Exception)
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e) {
        log.error("系统异常：", e);
        return ResponseResult.error("系统繁忙，请稍后再试");
    }
}

