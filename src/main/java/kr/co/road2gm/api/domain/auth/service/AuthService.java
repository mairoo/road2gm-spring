package kr.co.road2gm.api.domain.auth.service;

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
    signIn(PasswordGrantRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }

        // 액세스 토큰은 별도로 서버에 저장 안 하고 JSON 응답
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername());

        return Optional.of(new AccessTokenResponse(accessToken, accessTokenValidity));
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
    refresh() {

        //  Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshToken);
        //        if (refreshTokenOpt.isEmpty()) {
        //            return ResponseEntity.badRequest().body("Invalid refresh token");
        //        }
        //
        //        RefreshToken refreshTokenEntity = refreshTokenOpt.get();
        //        if (refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
        //            refreshTokenRepository.delete(refreshTokenEntity);
        //            return ResponseEntity.badRequest().body("Refresh token expired");
        //        }
        //
        //        User user = refreshTokenEntity.getUser();
        //        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        //        final String accessToken = jwtUtil.generateToken(userDetails, true);
        //

        // 토큰 이름 = 일치 시 토큰 가져옴
        // 만료된 것이면 삭제처리 하고 오류 throw
        // 토큰 이름으로 사용자명 가져옴
        // 사용자명으로 이름 검색
        // 액세스 토큰 발급
        return Optional.empty();
    }
}
