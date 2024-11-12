package kr.co.road2gm.api.domain.auth.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.road2gm.api.global.common.entity.BaseDateTime;
import kr.co.road2gm.api.global.security.oauth2.entity.enums.SocialProvider;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "social_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class SocialAccount extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Column(name = "provider")
    @Enumerated(value = EnumType.STRING)
    private SocialProvider provider;

    @Column(name = "uid")
    private String uid;

    @Column(name = "extra_data")
    private String extraData;

    public static SocialAccountBuilder from(User user, SocialProvider provider, String uid) {
        return new SocialAccountBuilder()
                .user(user)
                .provider(provider)
                .uid(uid);
    }

    public void connect(User user) {
        this.user = user;
    }
}
