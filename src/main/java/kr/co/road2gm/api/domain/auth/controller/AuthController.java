package kr.co.road2gm.api.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.road2gm.api.domain.auth.controller.request.PasswordGrantRequest;
import kr.co.road2gm.api.domain.auth.service.AuthService;
import kr.co.road2gm.api.global.common.ApiResponse;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.ErrorResponse;
import kr.co.road2gm.api.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expires-in}")
    private int refreshTokenValidity;

    @PostMapping("/authenticate")
    public ResponseEntity<?>
    login(@Valid @RequestBody
          PasswordGrantRequest request,
          HttpServletRequest servletRequest,
          HttpServletResponse servletResponse) {
        return authService.authenticate(request, servletRequest, servletResponse)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(ErrorResponse.of(ErrorCode.WRONG_USERNAME_OR_PASSWORD,
                                                                 servletRequest.getRequestURI()))));
    }
}
