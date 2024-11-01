package kr.co.road2gm.api.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class RequestHeaderParser {
    @Value("${web-config.real-ip-header}")
    private String realIpHeader;

    private HttpServletRequest httpServletRequest;

    public RequestHeaderParser changeHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        return this;
    }

    public String getIpAddress() {
        return Optional.ofNullable(httpServletRequest.getHeader(realIpHeader))
                .orElse(httpServletRequest.getRemoteAddr());
    }

    public String getUserAgent() {
        return Optional.ofNullable(httpServletRequest.getHeader("User-Agent"))
                .orElse("No user-agent set");
    }

    public String getAcceptLanguage() {
        return Optional.ofNullable(httpServletRequest.getHeader("Accept-Language"))
                .orElse("No language set");
    }
}
