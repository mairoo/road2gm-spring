package kr.co.road2gm.api.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import kr.co.road2gm.api.global.util.RequestHeaderParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    private final CookieService cookieService;

    private final RequestHeaderParser requestHeaderParser;

    @PostMapping("/sign-in")
    public ResponseEntity<?>
    signIn(@Valid @RequestBody
           PasswordGrantRequest request,
           HttpServletRequest servletRequest) {
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
                    RequestHeaderParser headerParser = requestHeaderParser.changeHttpServletRequest(servletRequest);

                    // 리프레시 토큰 생성
                    String refreshToken = authService.issueRefreshToken(request.getUsername(),
                                                                        headerParser.getIpAddress());

                    // 리프레시 쿠키 전송 설정
                    ResponseCookie cookie = cookieService.create(refreshToken);

                    // JWT 액세스 토큰 응답 객체 반환
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(ApiResponse.of(tokenResponse));
                })
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?>
    refresh(@CookieValue(name = "refreshToken") String refreshToken,
            HttpServletRequest servletRequest) {
        if (refreshToken == null) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }

        return authService.refresh().map(tokenResponse -> {
            RequestHeaderParser headerParser = requestHeaderParser.changeHttpServletRequest(servletRequest);

            // 리프레시 토큰 생성
            String newRefreshToken = authService.issueRefreshToken("username", headerParser.getIpAddress());

            // 리프레시 쿠키 전송 설정
            ResponseCookie cookie = cookieService.create(newRefreshToken);

            // JWT 액세스 토큰 응답 객체 반환
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(ApiResponse.of(tokenResponse));
        }).orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?>
    signOut() {
        // DB에 저장된 리프레시 토큰은 주기적인 배치 삭제 처리
        ResponseCookie cookie = cookieService.invalidate();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of(new LogoutResponse()));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?>
    signUp(@Valid @RequestBody SignUpRequest request) {
        // REST API 엔티티 생성 시 생성된 엔티티 정보 포함하여 반환
        //
        // - 클라이언트가 추가 요청 없이 생성된 사용자 정보 사용 가능
        // - 서버에서 생성된 ID, 시간 등의 정보 즉시 확인 가능
        // - REST 표준에 부합
        // - 생성 실패 시 상세한 에러 정보 전달 가능
        // - 응답 크기가 다소 커지고 비밀번호 같은 민감한 정보는 반드시 제외하도록 DTO 사용

        return authService.signUp(request)
                .map(user -> ResponseEntity.ok(ApiResponse.of(new UserResponse(user),
                                                              HttpStatus.CREATED,
                                                              "CREATED")))
                .orElseThrow(() -> new ApiException(ErrorCode.WRONG_USERNAME_OR_PASSWORD));

    }
}
