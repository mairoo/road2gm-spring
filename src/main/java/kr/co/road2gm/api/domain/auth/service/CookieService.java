package kr.co.road2gm.api.domain.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CookieService {
    @Value("${jwt.access-token-expires-in}")
    private int accessTokenValidity;

    @Value("${jwt.refresh-token-expires-in}")
    private int refreshTokenValidity;

    @Value("${jwt.cookie-domain}")
    private String cookieDomain;

    public ResponseCookie createAccessToken(String refreshToken) {
        return ResponseCookie.from("accessToken", refreshToken)
                .httpOnly(true) // XSS 공격 방지
                .secure(true) // 오직 HTTPS 허용
                .path("/") // 모든 경로에서 접근 가능
                .maxAge(accessTokenValidity)
                .sameSite("Strict") // CSRF 공격 방지
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie invalidateAccessToken() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    // 리프레시 토큰 HTTP only 쿠키 전송 보안 체크리스트
    // - 토큰 재사용 감지 및 대응
    // - 적절한 만료 시간 설정
    // - HTTPS 강제 사용
    // - Rate Limiting 구현
    // - 클라이언트 식별 정보 저장
    // - 보안 이벤트 모니터링
    // - 동시 세션 제한
    // - 토큰 순환(rotation) 구현
    // - 적절한 에러 처리
    // - 보안 헤더 설정

    public ResponseCookie createRefreshToken(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie invalidateRefreshToken() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }
}
