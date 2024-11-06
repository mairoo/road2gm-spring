package kr.co.road2gm.api.domain.auth.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordGrantRequest {
    // https://www.oauth.com/oauth2-servers/access-tokens/password-grant/
    @JsonProperty("username")
    @NotNull
    @NotBlank
    private String username;

    @JsonProperty("password")
    @NotNull
    @NotBlank
    private String password;

    @JsonProperty("rememberMe")
    @NotNull
    private boolean rememberMe;
}
