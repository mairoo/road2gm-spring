package kr.co.road2gm.api.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class TokenDto {
    private String accessToken;

    private String refreshToken;

    public TokenDto(String accessToken,
                    String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
