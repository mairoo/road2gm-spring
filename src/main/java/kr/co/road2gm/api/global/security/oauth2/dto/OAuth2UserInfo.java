package kr.co.road2gm.api.global.security.oauth2.dto;

public interface OAuth2UserInfo {
    String getProviderId();

    String getProvider();

    String getEmail();

    String getName();

    String getImageUrl();
}
