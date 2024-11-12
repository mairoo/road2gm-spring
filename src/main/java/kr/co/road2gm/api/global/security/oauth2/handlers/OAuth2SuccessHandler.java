package kr.co.road2gm.api.global.security.oauth2.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.road2gm.api.domain.auth.domain.OAuth2Token;
import kr.co.road2gm.api.domain.auth.domain.Role;
import kr.co.road2gm.api.domain.auth.domain.SocialAccount;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.domain.auth.domain.enums.RoleName;
import kr.co.road2gm.api.domain.auth.repository.jpa.OAuth2TokenRepository;
import kr.co.road2gm.api.domain.auth.repository.jpa.RoleRepository;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import kr.co.road2gm.api.domain.auth.service.CookieService;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.response.error.exception.ApiException;
import kr.co.road2gm.api.global.security.oauth2.dto.OAuth2UserInfo;
import kr.co.road2gm.api.global.security.oauth2.entity.OAuth2UserInfoFactory;
import kr.co.road2gm.api.global.security.oauth2.entity.enums.SocialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.oauth2-redirect-url}")
    private String frontendUrl;

    private final CookieService cookieService;

    private final OAuth2TokenRepository OAuth2TokenRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 소셜 로그인 인증 완료 후 작업 처리
        if (response.isCommitted()) {
            log.debug("Response has already been committed");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 소셜 로그인 제공자(구글, 네이버 등)로부터 받은 유저 정보 추출
        String registrationId = ((OAuth2AuthenticationToken) authentication)
                .getAuthorizedClientRegistrationId();

        // OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        // 2. 이메일로 유저 조회
        Optional<User> existingUser = userRepository.findByEmailWithSocialAccounts(userInfo.getEmail());
        User user = existingUser
                .map(value -> processExistingUser(value, userInfo))
                .orElseGet(() -> createNewUser(userInfo));

        // 3. JWT 토큰 또는 임시 state 해시값 생성
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

        log.error("token: {} email: {}", token, userInfo.getEmail());

        OAuth2TokenRepository.save(OAuth2Token.builder()
                                           .token(token)
                                           .email(user.getEmail())
                                           .build());

        ResponseCookie cookie = cookieService.createOAuth2Token(token);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(frontendUrl + "/auth/oauth2-redirect");
    }

    private User createNewUser(OAuth2UserInfo userInfo) {
        try {
            User newUser = User.from(userInfo.getName(), "", userInfo.getEmail()).build();

            // 기본 권한 부여
            Role userRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
            newUser.addRole(userRole);

            // 소셜 계정 연결
            SocialAccount socialAccount = SocialAccount.from(
                            newUser,
                            SocialProvider.valueOf(userInfo.getProvider().toUpperCase()),
                            userInfo.getProviderId())
                    .extraData(userInfo.getImageUrl())
                    .build();
            newUser.addSocialAccount(socialAccount);

            return userRepository.save(newUser);

        } catch (Exception e) {
            log.error("Error creating new user from OAuth2. email: {}, provider: {}",
                      userInfo.getEmail(), userInfo.getProvider(), e);
            throw new ApiException(ErrorCode.UNEXPECTED_ERROR);
        }
    }

    private User processExistingUser(User user, OAuth2UserInfo userInfo) {
        SocialProvider provider = SocialProvider.valueOf(userInfo.getProvider().toUpperCase());
        String providerId = userInfo.getProviderId();

        // 1-1. 이미 해당 소셜 계정으로 연결된 경우
        Optional<SocialAccount> socialAccount = user.getSocialConnection(provider);
        if (socialAccount.isPresent()) {
            if (!socialAccount.get().getUid().equals(providerId)) {
                log.error("Social provider {} already connected with different uid for email {}", provider,
                          user.getEmail());

                throw new ApiException(ErrorCode.OAUTH2_ACCOUNT_REGISTERED);
            }
            // 기존 연결된 계정이면 그대로 반환
            return user;
        }

        // 1-2. 다른 소셜 계정으로 가입된 경우
        if (!user.getSocialAccounts().isEmpty()) {
            log.error("Email {} already registered with different provider", user.getEmail());

            throw new ApiException(ErrorCode.OAUTH2_EMAIL_OCCUPIED);
        }

        // 1-3. 이메일만 있고 소셜 연결이 없는 경우 -> 소셜 계정 연결
        SocialAccount newSocialAccount = SocialAccount.from(user, provider, providerId)
                .extraData(userInfo.getImageUrl())
                .build();
        user.addSocialAccount(newSocialAccount);
        return userRepository.save(user);
    }
}
