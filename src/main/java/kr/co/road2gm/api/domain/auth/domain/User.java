package kr.co.road2gm.api.domain.auth.domain;

import jakarta.persistence.*;
import kr.co.road2gm.api.global.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class User extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    public static UserBuilder builder(String username,
                                      String password,
                                      String email) {
        return new UserBuilder()
                .username(username)
                .password(password)
                .email(email);
    }
}
