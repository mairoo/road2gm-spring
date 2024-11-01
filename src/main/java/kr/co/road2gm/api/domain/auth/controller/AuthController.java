package kr.co.road2gm.api.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.road2gm.api.domain.auth.controller.request.PasswordGrantRequest;
import kr.co.road2gm.api.domain.auth.controller.request.SignUpRequest;
import kr.co.road2gm.api.domain.auth.controller.response.LogoutResponse;
import kr.co.road2gm.api.domain.auth.controller.response.UserResponse;
import kr.co.road2gm.api.domain.auth.service.AuthService;
import kr.co.road2gm.api.domain.auth.service.CookieService;
import kr.co.road2gm.api.global.common.ApiResponse;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final CookieService cookieService;

    @PostMapping("/sign-in")
    public ResponseEntity<?>
    signIn(@Valid @RequestBody
          PasswordGrantRequest request,
           HttpServletRequest servletRequest,
           HttpServletResponse servletResponse) {
        // 예외 throw 방식 vs. 오류 객체 조건 분기 방식
        //
        // 예외 throw 방식의 장점 - 관심사의 분리, 코드의 분리, 유지보수, AOP 활용
        // 오류 객체 조건 분기 방식의 장점 - 명시적인 코드 흐름, 스택 트레이스 예외처리 오버헤드 없음

        // 액세스 토큰 JSON 응답에 ApiResponse 래퍼 사용 시
        // 장점: 확장성
        // - 일관된 응답 구조 유지
        // - 추가 메타 데이터 포함 가능
        // 단점:
        // - 응답 크기가 약간 증가하며 클라이언트에서 data 내부 접근하는 코드가 필요
        //
        // 단, 외부 API나 표준을 따라야 하는 경우(OAuth2 등)에는 감싸지 않는 편이 좋다.

        return authService.signIn(request)
                .map(tokenResponse -> {
                    log.error("header: {} remote addr: {}", servletRequest.getHeader(" X-Forwarded-For"),
                              servletRequest.getRemoteAddr());

                    // 리프레시 토큰 생성
                    String refreshToken = authService.issueRefreshToken(request.getUsername(), "127.0.0.1");

                    // 리프레시 쿠키 전송 설정
                    Cookie refreshTokenCookie = cookieService.create(refreshToken);

                    servletResponse.addCookie(refreshTokenCookie);

                    // JWT 액세스 토큰 응답 객체 반환
                    return ResponseEntity.ok(ApiResponse.of(tokenResponse));
                })
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?>
    signOut(HttpServletResponse servletResponse) {
        // DB에 저장된 리프레시 토큰은 주기적인 배치 삭제 처리
        Cookie cookie = cookieService.invalidate();

        servletResponse.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.of(new LogoutResponse()));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?>
    signUp(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request)
                .map(user -> ResponseEntity.ok(ApiResponse.of(new UserResponse(user),
                                                              HttpStatus.CREATED,
                                                              "CREATED")))
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

    }
}
