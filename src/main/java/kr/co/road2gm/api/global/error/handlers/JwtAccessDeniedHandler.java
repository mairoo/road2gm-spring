package kr.co.road2gm.api.global.error.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void
    handle(HttpServletRequest request, HttpServletResponse response,
           AccessDeniedException accessDeniedException) throws IOException {
        log.error("Access denied error: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), ErrorResponse.of(ErrorCode.ACCESS_DENIED,
                                                                       request.getRequestURI()));
    }
}
