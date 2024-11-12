package kr.co.road2gm.api.global.security.oauth2.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.road2gm.api.domain.auth.domain.OAuth2Token;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.global.security.oauth2.repository.OAuth2TokenRepository;
import kr.co.road2gm.api.domain.auth.service.CookieService;
import kr.co.road2gm.api.global.security.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.oauth2-redirect-url}")
    private String frontendUrl;

    private final CookieService cookieService;

    private final OAuth2TokenRepository OAuth2TokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        if (response.isCommitted()) {
            log.debug("Response has already been committed");
            return;
        }

        // 소셜 로그인 인증 완료 후 작업 처리
        // 1. 인증 완료 후 개인정보 추출
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.user();

        // 2. JWT 토큰 또는 임시 state 해시값 생성
        // 프론트엔드에 전달 방법
        // - JSON 응답: 프론트엔드에서 새창/팝업 띄우고 백엔드에서 JS 하드코딩 응답
        // - HttpOnly 쿠키 전송: 1분 만료, 도메인 및 경로 제한
        // - 쿼리 파라미터 전달: 리다이렉트로 구현 가능 (보안상 비추천)

        // 현재 구현방식: state Http only 쿠키 전송 후 리다이렉트 방식
        //
        // - 액세스 토큰 및 리프레시 토큰 생성 안 함
        // - 1분만료, 도메인/경로 제한 임시 state 생성 전달 후 리다이렉트

        // 임시 state 생성 및 저장
        String token = UUID.randomUUID().toString();

        OAuth2TokenRepository.save(OAuth2Token.builder()
                                           .token(token)
                                           .email(user.getEmail())
                                           .build());

        // 3. 리다이렉트
        ResponseCookie cookie = cookieService.createOAuth2Token(token);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(frontendUrl + "/auth/oauth2-redirect");
    }
}
