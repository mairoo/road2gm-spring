package kr.co.road2gm.api.domain.auth.service;

import kr.co.road2gm.api.domain.auth.controller.request.PasswordGrantRequest;
import kr.co.road2gm.api.domain.auth.controller.response.AccessTokenResponse;
import kr.co.road2gm.api.domain.auth.domain.RefreshToken;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.domain.auth.repository.jpa.RefreshTokenRepository;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.exception.ApiException;
import kr.co.road2gm.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${jwt.access-token-expires-in}")
    private int accessTokenValidity;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Optional<AccessTokenResponse>
    authenticate(PasswordGrantRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }

        // 액세스 토큰은 별도로 서버에 저장 안 하고 JSON 응답
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());

        return Optional.of(new AccessTokenResponse(accessToken, accessTokenValidity));
    }

    public String
    issueRefreshToken(String username, String ipAddress) {
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 리프레시 토큰은 HttpOnly, Secure, SameSite=Strict 전송, RDBMS 또는 Redis 저장
        refreshTokenRepository.save(RefreshToken.builder(refreshToken, username, ipAddress).build());

        return refreshToken;
    }
}
