package kr.co.road2gm.api.global.oauth2.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.road2gm.api.domain.auth.domain.SocialAccountState;
import kr.co.road2gm.api.domain.auth.repository.jpa.SocialAccountStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.oauth2-redirect-url}")
    private String frontendUrl;

    private final SocialAccountStateRepository socialAccountStateRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // onAuthenticationSuccess 메소드 실행 시점
        //
        // - 인증이 완료 된 이후 (인증이 완료된 사용자 정보 authentication 객체 받음)

        // onAuthenticationSuccess 메소드 처리 업무
        //
        // - JWT 토큰 생성
        // - 쿠키 설정
        // - 리다이렉트 처리
        // - 클라이언트에 응답 전송

        // 백엔드의 JWT 액세스 토큰 또는 state를 프론트엔드에 전달하는 방법
        //
        // 1. JSON 응답
        // - 새 창, 팝업으로 구현
        // 2. HTTP only 쿠키 전송
        // - 리다이렉트 구현 가능, 백엔드가 유효기간 1분 쿠키와 bearer 토큰을 모두 받는 형태로 구현해야 함
        // 3. Authorization Code + state 전송
        // - 리다이렉트 구현 가능, 백엔드가 유효기간 1분 state 값을 삭제처리해야 함(redis)
        // 4. 쿼리 파라미터 전달
        // - 리다이렉트 구현 가능 - 웹서버 access 로그와 브라우지 히스토리에 토큰이 남음 (보안상 비추천)

        // 백엔드 (스프링부트) - http://localhost:8080/login/oauth2/code/google
        //
        // - OAuth2 인증 후 Google이 실제로 리다이렉트하는 콜백 URL
        // - Spring Security에서 처리하는 엔드포인트
        // - 인증 코드를 받아서 처리하고 JWT 토큰 또는 임시 state 생성

        // 프론트엔드 (React) - http://localhost:3000/login/oauth2/code/google
        //
        // - React Router에서 처리하는 클라이언트 측 라우트
        // - 백엔드에서 생성된 JWT 토큰 또는 state를 받아서 저장하고 처리하는 페이지
        // - 실제 OAuth2 인증과는 무관

        if (response.isCommitted()) {
            log.debug("Response has already been committed");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        // 현재 구현방식: state query param 전달 리다이렉트 방식
        //
        // - 액세스 토큰 및 리프레시 토큰 생성 안 함
        // - 1분만료 임시 state 생성 전달 후 리다이렉트

        // 임시 state 생성 및 저장
        String state = UUID.randomUUID().toString();
        socialAccountStateRepository.save(SocialAccountState.builder()
                                                  .state(state)
                                                  .email(email)
                                                  .build());

        // 프론트엔드로 state 전달
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/oauth2-redirect")
                .queryParam("state", state)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}
