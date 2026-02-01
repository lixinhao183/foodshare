package xinhao.foodshare.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.utils.WebUtils;

import java.io.IOException;

/* 访问拒绝处理 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseResult result = ResponseResult.error(403, "您的权限不足");
        String json = OBJECT_MAPPER.writeValueAsString(result);
        //处理异常
        WebUtils.renderString(response, json);
    }
}
