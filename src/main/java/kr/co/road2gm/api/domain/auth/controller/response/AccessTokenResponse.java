package kr.co.road2gm.api.domain.auth.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenResponse {
    // https://www.oauth.com/oauth2-servers/access-tokens/access-token-response/

    // API 응답 JSON 키 형식 - 일반적으로 camelCase 추천
    //
    // JavaScript/TypeScript 사용 시 camelCase 선호
    // Python/Ruby 사용 시 snake_case 선호

    @JsonProperty("accessToken")
    @NotNull
    @NotBlank
    private String accessToken;

    @JsonProperty("tokenType")
    @NotNull
    @NotBlank
    private String tokenType;

    @JsonProperty("expiresIn")
    @NotNull
    private Integer expiresIn;

    @JsonIgnore
    private String refreshToken;

    // 보안을 위해 액세스 토큰 응답에 리프레시 토큰은 내려주지 않고 쿠키 전송
    // - XSS 공격으로부터 리프레시 토큰 보호
    // - CSRF 공격은 SameSite=Strict로 방어
    // - 클라이언트에서 리프레시 토큰 관리 필요 없음
    // - 표준 OAuth2 패턴과 일치

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scope;

    public AccessTokenResponse(String accessToken,
                               Integer expiresIn) {
        this.tokenType = "Bearer";
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public AccessTokenResponse(String accessToken,
                               Integer expiresIn,
                               String refreshToken) {
        this(accessToken, expiresIn);
        this.refreshToken = refreshToken;
    }
}
