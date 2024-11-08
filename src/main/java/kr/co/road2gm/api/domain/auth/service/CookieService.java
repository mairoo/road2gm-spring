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
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public static final String OAUTH2_TOKEN_COOKIE_NAME = "oauth2_token";

    @Value("${jwt.refresh-token-expires-in}")
    private int refreshTokenValidity;

    @Value("${jwt.cookie-domain}")
    private String cookieDomain;

    public ResponseCookie createRefreshToken(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie invalidateRefreshToken() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie createOAuth2Token(String oauth2Token) {
        return ResponseCookie.from(OAUTH2_TOKEN_COOKIE_NAME, oauth2Token)
                .httpOnly(true)
                .secure(true)
                .path("/auth/oauth2-token") // 특정 엔드포인트로 제한
                .maxAge(Duration.ofMinutes(1)) // 1분 후 만료
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }

    public ResponseCookie invalidateOAuth2Token() {
        return ResponseCookie.from(OAUTH2_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .domain(cookieDomain)
                .build();
    }
}
