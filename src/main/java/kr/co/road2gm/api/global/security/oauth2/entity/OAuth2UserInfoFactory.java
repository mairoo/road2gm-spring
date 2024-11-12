package kr.co.road2gm.api.global.security.oauth2.entity;

import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.response.error.exception.ApiException;
import kr.co.road2gm.api.global.security.oauth2.dto.OAuth2UserInfo;
import kr.co.road2gm.api.global.security.oauth2.entity.enums.SocialProvider;
import kr.co.road2gm.api.global.security.oauth2.info.FacebookOAuth2UserInfo;
import kr.co.road2gm.api.global.security.oauth2.info.GoogleOAuth2UserInfo;
import kr.co.road2gm.api.global.security.oauth2.info.KakaoOAuth2UserInfo;
import kr.co.road2gm.api.global.security.oauth2.info.NaverOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(SocialProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialProvider.FACEBOOK.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialProvider.NAVER.toString())) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SocialProvider.KAKAO.toString())) {
            return new KakaoOAuth2UserInfo(attributes);
        } else {
            throw new ApiException(ErrorCode.OAUTH2_PROVIDER_NOT_FOUND);
        }
    }
}
