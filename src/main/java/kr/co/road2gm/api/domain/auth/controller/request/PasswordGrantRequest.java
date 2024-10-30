package kr.co.road2gm.api.domain.auth.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordGrantRequest {
    // https://www.oauth.com/oauth2-servers/access-tokens/password-grant/

    @JsonProperty("grantType")
    @NotNull
    @Pattern(regexp = "password", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid grant type")
    private String grantType;

    @JsonProperty("username")
    @NotNull
    @NotBlank
    private String username;

    @JsonProperty("password")
    @NotNull
    @NotBlank
    private String password;

    @JsonProperty("captcha")
    @NotNull
    @NotBlank
    private String captcha;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("clientSecret")
    private String clientSecret;
}
