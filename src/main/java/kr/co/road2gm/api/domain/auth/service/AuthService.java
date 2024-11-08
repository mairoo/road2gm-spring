package kr.co.road2gm.api.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.road2gm.api.domain.auth.controller.request.AccessTokenRequest;
import kr.co.road2gm.api.domain.auth.controller.request.SignUpRequest;
import kr.co.road2gm.api.domain.auth.domain.*;
import kr.co.road2gm.api.domain.auth.domain.enums.RoleName;
import kr.co.road2gm.api.domain.auth.dto.TokenDto;
import kr.co.road2gm.api.domain.auth.repository.jpa.*;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.exception.ApiException;
import kr.co.road2gm.api.global.jwt.JwtTokenProvider;
import kr.co.road2gm.api.global.util.RequestHeaderParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final SocialAccountStateRepository socialAccountStateRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final RequestHeaderParser requestHeaderParser;

    @Transactional
    public Optional<TokenDto>
    signIn(AccessTokenRequest request, HttpServletRequest servletRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        String refreshToken = issueRefreshToken(request.getEmail(), requestHeaderParser.getClientIp(servletRequest));

        return Optional.of(new TokenDto(accessToken, refreshToken));
    }

    @Transactional
    public Optional<User>
    signUp(SignUpRequest request) {
        // 복합 인덱스 사용 및 findByUsernameOrEmail() 메소드는 수만건 이하 데이터 조회에 적합

        // RDBMS 개별 인덱스 설정: username, email 설정
        // 개별 쿼리 실행으로 수십만건 데이터까지는 문제 없음

        // 회원수 수백만일 경우에는 캐시, 인덱스 힌트 등 사용
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXIST);
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        User user = User.from(request.getUsername(),
                              passwordEncoder.encode(request.getPassword()),
                              request.getEmail())
                .build();

        userRepository.save(user);

        return Optional.of(user);
    }

    @Transactional
    public Optional<TokenDto>
    refresh(String refreshToken, HttpServletRequest servletRequest) {
        RefreshToken oldRefreshToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new ApiException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));

        String email = oldRefreshToken.getEmail();

        // 기존 리프레시 토큰 즉시 삭제 (재사용 방지)
        int deletedCount = refreshTokenRepository.deleteAllByEmail(email);
        log.info("Deleted {} refresh token by {}", deletedCount, email);

        // 사용자 조회
        User user = userRepository.findByUsername(oldRefreshToken.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        // 리프레시 토큰 로테이션 (재발급 처리)
        String newRefreshToken = issueRefreshToken(user.getEmail(), requestHeaderParser.getClientIp(servletRequest));

        return Optional.of(new TokenDto(accessToken, newRefreshToken));
    }

    @Transactional
    public Optional<TokenDto>
    signIn(String state, HttpServletRequest servletRequest) {
        SocialAccountState authState = socialAccountStateRepository.findByState(state)
                .orElseThrow(() -> new IllegalArgumentException("Invalid state"));

        if (authState.isExpired()) {
            socialAccountStateRepository.delete(authState);
            throw new IllegalArgumentException("Expired state");
        }

        String email = authState.getEmail();

        // 사용한 state 삭제
        socialAccountStateRepository.delete(authState);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = issueRefreshToken(email, requestHeaderParser.getClientIp(servletRequest));

        return Optional.of(new TokenDto(accessToken, refreshToken));
    }

    @Transactional
    public void
    addRoleToUser(String username, RoleName roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("역할 없음"));

        if (userRoleRepository.findByUserAndRole(user, role).isEmpty()) {
            UserRole userRole = UserRole.from(user, role).build();
            userRoleRepository.save(userRole);
        }
    }

    @Transactional
    public void
    removeRoleFromUser(String username, RoleName roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("역할 없음"));

        userRoleRepository.deleteByUserAndRole(user, role);
    }

    public String
    issueRefreshToken(String username, String ipAddress) {
        String refreshToken = jwtTokenProvider.createRefreshToken();

        refreshTokenRepository.save(RefreshToken.from(refreshToken, username, ipAddress).build());

        return refreshToken;
    }
}
