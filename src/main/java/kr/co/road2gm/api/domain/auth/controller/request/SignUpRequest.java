package kr.co.road2gm.api.domain.auth.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {
    @JsonProperty("username")
    @NotNull
    @NotBlank
    private String username;

    @JsonProperty("password")
    @NotNull
    @NotBlank
    private String password;
}
