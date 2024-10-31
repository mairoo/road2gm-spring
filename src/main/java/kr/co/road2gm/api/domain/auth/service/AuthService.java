package kr.co.road2gm.api.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.road2gm.api.domain.auth.controller.request.PasswordGrantRequest;
import kr.co.road2gm.api.domain.auth.controller.response.AccessTokenResponse;
import kr.co.road2gm.api.domain.auth.repository.jpa.RefreshTokenRepository;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public Optional<AccessTokenResponse>
    authenticate(PasswordGrantRequest request,
                 HttpServletRequest servletRequest,
                 HttpServletResponse servletResponse) {

        // 컨트롤러에서 오류는 오류 응답 객체 분기하여 반환 처리
        // 서비스에서 오류는 그대로 예외 throw 후 GlobalExceptionHandler 메소드에서 처리

        return Optional.empty();
    }

    public void issueAccessToken() {

    }

    public void issueRefreshToken() {

    }
}
