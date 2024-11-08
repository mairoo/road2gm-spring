package kr.co.road2gm.api.domain.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CookieService {
    public static final String accessTokenCookieName = "access_token";

    public static final String refreshTokenCookieName = "refresh_token";

    public static final String socialAccountStateCookieName = "oauth2_state";

    @Value("${jwt.access-token-expires-in}")
    private int accessTokenValidity;

    @Value("${jwt.refresh-token-expires-in}")
    private int refreshTokenValidity;

    @Value("${jwt.cookie-domain}")
    private String cookieDomain;

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
        return ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie invalidateRefreshToken() {
        return ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie createSocialAccountState(String state) {
        return ResponseCookie.from("oauth2_state", state)
                .httpOnly(true)
                .secure(true)
                .path("/auth/oauth2-state") // 특정 엔드포인트로 제한
                .maxAge(Duration.ofMinutes(1)) // 1분 후 만료
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie invalidateSocialAccountState() {
        return ResponseCookie.from(socialAccountStateCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }
}
