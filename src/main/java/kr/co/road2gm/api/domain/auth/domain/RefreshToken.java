package kr.co.road2gm.api.domain.auth.domain;

import jakarta.persistence.*;
import kr.co.road2gm.api.global.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class RefreshToken extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 리프레시 토큰은 역정규화(deNormalization) - 향후 Redis 기반으로 변경될 수 있음
    @Column(name = "username")
    private String username;

    @Column(name = "token")
    private String token;

    @Column(columnDefinition = "CHAR(39)")
    private String ipAddress;

    public static RefreshTokenBuilder from(String token, String username, String ipAddress) {
        return new RefreshTokenBuilder()
                .username(username)
                .token(token)
                .ipAddress(ipAddress);
    }
}
