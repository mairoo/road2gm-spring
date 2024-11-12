package kr.co.road2gm.api.global.security.oauth2.service;

import kr.co.road2gm.api.domain.auth.domain.Role;
import kr.co.road2gm.api.domain.auth.domain.SocialAccount;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.domain.auth.domain.enums.RoleName;
import kr.co.road2gm.api.domain.auth.repository.jpa.RoleRepository;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.response.error.exception.ApiException;
import kr.co.road2gm.api.global.security.oauth2.dto.CustomOAuth2User;
import kr.co.road2gm.api.global.security.oauth2.dto.OAuth2UserInfo;
import kr.co.road2gm.api.global.security.oauth2.entity.OAuth2UserInfoFactory;
import kr.co.road2gm.api.global.security.oauth2.entity.enums.SocialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(),
                                   oauth2User.getAttributes());

        // 이메일로 유저 조회 및 처리
        Optional<User> existingUser = userRepository.findByEmailWithSocialAccounts(userInfo.getEmail());
        User user = existingUser
                .map(value -> processExistingUser(value, userInfo))
                .orElseGet(() -> createNewUser(userInfo));

        // OAuth2User를 반환하되, User 엔티티 정보도 함께 포함
        return new CustomOAuth2User(oauth2User, user);
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
