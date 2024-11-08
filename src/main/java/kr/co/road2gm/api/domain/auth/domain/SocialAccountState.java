package kr.co.road2gm.api.domain.auth.domain;

import jakarta.persistence.*;
import kr.co.road2gm.api.global.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_account_state")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class SocialAccountState extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // state 값은 역정규화(deNormalization) - 향후 Redis 기반으로 변경될 수 있음
    @Column(name = "email")
    private String email;

    @Column(name = "state")
    private String state;

    public static SocialAccountStateBuilder from(String email, String state) {
        return new SocialAccountStateBuilder()
                .email(email)
                .state(state);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(getCreated().plusMinutes(1));
    }
}
