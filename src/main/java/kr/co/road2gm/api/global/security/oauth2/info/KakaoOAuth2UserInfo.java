package kr.co.road2gm.api.global.security.oauth2.info;

import kr.co.road2gm.api.global.security.oauth2.dto.OAuth2UserInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final Map<String, Object> attributesAccount;
    private final Map<String, Object> attributesProfile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String) attributesAccount.get("email");
    }

    @Override
    public String getName() {
        return (String) attributesProfile.get("nickname");
    }

    @Override
    public String getImageUrl() {
        return (String) attributesProfile.get("profile_image_url");
    }
}
