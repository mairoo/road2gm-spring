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
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(accessTokenValidity)
                .sameSite("Strict")
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
