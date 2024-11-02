package kr.co.road2gm.api.domain.auth.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RefreshTokenDto {
    private String refreshToken;

    private String username;

    private String ipAddress;
}
