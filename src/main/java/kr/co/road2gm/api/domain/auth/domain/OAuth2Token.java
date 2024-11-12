package kr.co.road2gm.api.domain.auth.domain;

import jakarta.persistence.*;
import kr.co.road2gm.api.global.common.entity.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth2_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class OAuth2Token extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // token 값은 역정규화(deNormalization) - 향후 Redis 기반으로 변경될 수 있음
    @Column(name = "email")
    private String email;

    @Column(name = "token")
    private String token;

    public static OAuth2TokenBuilder from(String email, String token) {
        return new OAuth2TokenBuilder()
                .email(email)
                .token(token);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(getCreated().plusMinutes(1));
    }
}
