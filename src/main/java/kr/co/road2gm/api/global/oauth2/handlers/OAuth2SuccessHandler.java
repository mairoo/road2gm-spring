package kr.co.road2gm.api.global.oauth2.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.road2gm.api.domain.auth.controller.response.AccessTokenResponse;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import kr.co.road2gm.api.global.common.ApiResponse;
import kr.co.road2gm.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;

    private final JwtTokenProvider tokenProvider;

    private final ObjectMapper objectMapper; // 스프링 자동 주입

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 백엔드 (스프링부트) - http://localhost:8080/login/oauth2/code/google
        //
        // - OAuth2 인증 후 Google이 실제로 리다이렉트하는 콜백 URL
        // - Spring Security에서 처리하는 엔드포인트
        // - 인증 코드를 받아서 처리하고 JWT 토큰 생성

        // 프론트엔드 (React) - http://localhost:3000/login/oauth2/code/google
        //
        // - React Router에서 처리하는 클라이언트 사이드 라우트
        // - 백엔드에서 생성된 JWT 토큰을 받아서 저장하고 처리하는 페이지
        // - 실제 OAuth2 인증과는 무관

        // 여기서는 굳이 복잡하게 리다이렉트 하지 않고 경로 일치

        // Query Parameter 토큰 전달 방식의 문제점:
        //
        // - 브라우저 히스토리에 토큰이 남음
        // - URL에 토큰이 노출됨
        // - 서버 로그에 토큰이 남을 수 있음
        // - 외부로 URL 공유 시 토큰도 함께 노출

        // HttpOnly Cookie + CSRF 토큰:
        //
        // - XSS 공격 방지
        // - CSRF 공격 방지
        // - 클라이언트에서 토큰 관리 불필요

        // Authorization Code + State:
        //
        // - 토큰이 URL에 노출되지 않음
        // - 임시 코드는 1회성이며 짧은 유효기간
        // - Redis 등으로 안전하게 관리 가능

        // 권장사항: HTTP Only 쿠키 + 추가 보안 조치
        // - AccessToken은 HTTP Only 쿠키로 저장
        // - CSRF 토큰 사용
        // - Secure 플래그 설정 (HTTPS)
        // - SameSite 속성 설정
        // - 적절한 만료 시간 설정
        // - RefreshToken 사용 시 별도 보안 고려

        if (response.isCommitted()) {
            log.debug("Response has already been committed");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");

//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalStateException("User not found with email: " + email));

        String token = tokenProvider.createAccessToken(email);

        ApiResponse<AccessTokenResponse> apiResponse = ApiResponse.of(new AccessTokenResponse(token, 1800),
                                                                      HttpStatus.OK,
                                                                      "로그인에 성공했습니다.");

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
