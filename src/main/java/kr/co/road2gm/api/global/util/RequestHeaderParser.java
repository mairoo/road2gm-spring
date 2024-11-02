package kr.co.road2gm.api.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@Slf4j
public class RequestHeaderParser {
    // 불변 유틸리티 클래스
    // 스프링 빈은 기본적으로 싱글톤
    // 여러 스레드가 동시에 같은 인스턴스 필드를 변경하려고 할 때 동시성 문제 있을 수 있음

    public String getUserAgent(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("User-Agent"))
                .orElse("No user-agent set");
    }

    public Locale getLocale(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Accept-Language"))
                .map(Locale::forLanguageTag)
                .orElse(Locale.getDefault());
    }

    public String getClientIp(HttpServletRequest request) {
        // CF-Connecting-IP: Cloudflare에서 제공하는 신뢰할 수 있는 클라이언트 IP
        String clientIp = request.getHeader("CF-Connecting-IP");

        if (clientIp == null) {
            // Proxy-Client-IP: 일부 프록시 서버에서 사용, 보통 단일 IP 주소만 포함
            clientIp = request.getHeader("Proxy-Client-IP");
        }

        if (clientIp == null) {
            // X-Forwarded-For: 프록시나 로드밸런서를 통과하면서 클라이언트의 원본 IP를 보존
            clientIp = request.getHeader("X-Forwarded-For");
        }

        if (clientIp == null) {
            // getRemoteAddr(): 직접 연결된 클라이언트의 IP 주소, 프록시나 로드밸런서 사용 시 프록시의 IP가 반환됨
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }

}
