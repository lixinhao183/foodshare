package xinhao.foodshare.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import xinhao.foodshare.result.ResponseResult;
import xinhao.foodshare.utils.WebUtils;

import java.io.IOException;

/* 认证失败处理 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseResult result = ResponseResult.error(401, "认证失败，请重新登录");
        String json = OBJECT_MAPPER.writeValueAsString(result);
        //处理异常
        WebUtils.renderString(response, json);
    }
}
