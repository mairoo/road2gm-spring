package kr.co.road2gm.api.domain.auth.dto;

import kr.co.road2gm.api.domain.auth.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class TokenDto {
    // https://www.oauth.com/oauth2-servers/access-tokens/access-token-response/
    private String accessToken;

    private String refreshToken;

    private String username;

    private String email;

    public TokenDto(String accessToken,
                    String refreshToken,
                    User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
