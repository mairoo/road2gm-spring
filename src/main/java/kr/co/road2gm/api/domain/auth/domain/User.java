package kr.co.road2gm.api.domain.auth.domain;

import jakarta.persistence.*;
import kr.co.road2gm.api.global.security.oauth2.entity.enums.SocialProvider;
import kr.co.road2gm.api.global.common.entity.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Jackson 직렬화를 위해 기본 생성자 필요, 직접적인 생성자 호출 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더에서만 사용하도록 제한, 직접적인 생성자 호출 방지
@Builder // 객체 생성을 위한 명확한 방법 제공, 필요한 필드만 선택적으로 설정 가능
@Getter // 불변 객체로 만들기 위해 Setter 제외, JSON 직렬화를 위해 Getter 필요
@Slf4j
public class User extends BaseDateTime implements UserDetails  {
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

    @Column(name = "remember_me")
    private boolean rememberMe;

    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<SocialAccount> socialAccounts = new HashSet<>();

    public static UserBuilder from(String username,
                                   String password,
                                   String email) {
        return new UserBuilder()
                .username(username)
                .password(password)
                .email(email)
                .rememberMe(false);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    // Role 연동 메소드
    public void addRole(Role role) {
        UserRole userRole = UserRole.from(this, role).build();
        userRoles.add(userRole);
    }

    public void removeRole(Role role) {
        userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }

    // 소셜 계정 연동
    public void addSocialAccount(SocialAccount account) {
        socialAccounts.add(account);
        account.connect(this);
    }

    public void removeSocialAccount(SocialProvider provider) {
        socialAccounts.removeIf(account -> account.getProvider() == provider);
    }

    public Optional<SocialAccount> getSocialConnection(SocialProvider provider) {
        return socialAccounts.stream()
                .filter(connection -> connection.getProvider() == provider)
                .findFirst();
    }
}
