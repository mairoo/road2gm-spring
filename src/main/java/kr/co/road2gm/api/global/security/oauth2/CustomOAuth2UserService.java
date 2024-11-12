package kr.co.road2gm.api.global.security.oauth2;

import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 구글에서 가져온 정보로 사용자 생성 또는 업데이트
        Map<String, Object> attributes = oauth2User.getAttributes();
        log.debug((String) attributes.get("sub"));
        log.debug((String) attributes.get("email"));
        log.debug((String) attributes.get("name"));
        log.debug((String) attributes.get("picture"));

        return oauth2User;  // 기본 OAuth2User 그대로 반환
    }
}
