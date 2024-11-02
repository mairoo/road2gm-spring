package kr.co.road2gm.api.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.road2gm.api.domain.auth.controller.request.PasswordGrantRequest;
import kr.co.road2gm.api.domain.auth.controller.request.SignUpRequest;
import kr.co.road2gm.api.domain.auth.controller.response.AccessTokenResponse;
import kr.co.road2gm.api.domain.auth.domain.RefreshToken;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.domain.auth.repository.jpa.RefreshTokenRepository;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.exception.ApiException;
import kr.co.road2gm.api.global.jwt.JwtTokenProvider;
import kr.co.road2gm.api.global.util.RequestHeaderParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${jwt.access-token-expires-in}")
    private int accessTokenValidity;

    @Value("${jwt.refresh-token-expires-in}")
    private int refreshTokenValidity;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final RequestHeaderParser requestHeaderParser;

    @Transactional
    public Optional<AccessTokenResponse>
    signIn(PasswordGrantRequest request, HttpServletRequest servletRequest) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }

        // 액세스 토큰은 별도로 서버에 저장 안 하고 JSON 응답
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());

        // 리프레시 토큰 생성
        String refreshToken = issueRefreshToken(request.getUsername(), requestHeaderParser.getClientIp(servletRequest));

        return Optional.of(new AccessTokenResponse(accessToken, accessTokenValidity, refreshToken));
    }

    @Transactional
    public Optional<User>
    signUp(SignUpRequest request) {
        // 복합 인덱스 사용 및 findByUsernameOrEmail() 메소드는 수만건 이하 데이터 조회에 적합

        // RDBMS 개별 인덱스 설정: username, email 설정
        // 개별 쿼리 실행으로 수십만건 데이터까지는 처리 가능

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXIST);
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        User user = User.builder(request.getUsername(),
                                 passwordEncoder.encode(request.getPassword()),
                                 request.getEmail())
                .build();

        userRepository.save(user);

        return Optional.of(user);
    }

    public String
    issueRefreshToken(String username, String ipAddress) {
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 리프레시 토큰은 HttpOnly, Secure, SameSite=Strict 전송, RDBMS 또는 Redis 저장
        refreshTokenRepository.save(RefreshToken.builder(refreshToken, username, ipAddress).build());

        return refreshToken;
    }

    @Transactional
    public Optional<AccessTokenResponse>
    refresh(String refreshToken, HttpServletRequest servletRequest) {
        RefreshToken oldRefreshToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new ApiException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));

        // 리프레시 토큰 만료 여부 확인
        if (ChronoUnit.SECONDS.between(oldRefreshToken.getCreated(), LocalDateTime.now()) > refreshTokenValidity) {
            refreshTokenRepository.delete(oldRefreshToken);
            throw new ApiException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 사용자 조회
        User user = userRepository.findByUsername(oldRefreshToken.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        // 액세스 토큰은 별도로 서버에 저장 안 하고 JSON 응답
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());

        // 리프레시 토큰 생성
        String newRefreshToken = issueRefreshToken(user.getUsername(), requestHeaderParser.getClientIp(servletRequest));

        return Optional.of(new AccessTokenResponse(accessToken, accessTokenValidity, newRefreshToken));
    }
}
