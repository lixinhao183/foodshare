package xinhao.foodshare.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import xinhao.foodshare.result.ResponseResult;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获文件上传大小超出限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseResult maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
        log.error("文件上传异常：单个文件大小不得超过10MB，总请求大小不得超过100MB");
        return ResponseResult.error("文件上传失败：单个文件不能超过10MB");
    }

    /**
     * 捕获业务异常 (RuntimeException) * 捕获业务异常 (RuntimeException)
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

