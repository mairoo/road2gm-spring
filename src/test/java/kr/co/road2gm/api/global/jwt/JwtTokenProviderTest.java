package kr.co.road2gm.api.global.jwt;

import kr.co.road2gm.api.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.profiles.active=test")
class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void checkToken() {
        String accessToken = jwtTokenProvider.createAccessToken("admin");
        String refreshToken = jwtTokenProvider.createRefreshToken();

        assertThat(accessToken).isNotBlank();
        assertThat(refreshToken).isNotBlank();

        assertThat(jwtTokenProvider.validateToken(accessToken)).hasValue("admin");
    }
}