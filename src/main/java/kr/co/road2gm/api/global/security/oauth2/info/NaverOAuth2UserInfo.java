package kr.co.road2gm.api.global.security.oauth2.info;

import kr.co.road2gm.api.global.security.oauth2.dto.OAuth2UserInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class NaverOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image");
    }
}

