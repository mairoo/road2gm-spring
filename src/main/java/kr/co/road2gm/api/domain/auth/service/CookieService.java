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
    @Value("${jwt.refresh-token-expires-in}")
    private int refreshTokenValidity;

    public ResponseCookie create(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie invalidate() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenValidity)
                .sameSite("Strict").build();
    }
}
