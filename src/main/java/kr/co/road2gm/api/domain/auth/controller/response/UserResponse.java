package kr.co.road2gm.api.domain.auth.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.road2gm.api.domain.auth.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class UserResponse {
    @JsonProperty("username")
    @NotNull
    @NotBlank
    private String username;

    @JsonProperty("email")
    @NotNull
    @NotBlank
    private String email;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
