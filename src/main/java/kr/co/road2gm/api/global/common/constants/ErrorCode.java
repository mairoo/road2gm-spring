package kr.co.road2gm.api.global.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력 필드 검증 실패", "필드 입력값 양식이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스 없음", "요청 경로가 올바르지 않습니다."),
    MESSAGE_BODY_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청 본문 없음", "HTTP 요청 본문이 없거나 올바르지 않습니다."),
    JSON_SERIALIZATION_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "응답 객체 직렬화 실패", "응답 객체를 JSON 직렬화하지 못했습니다"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 오류", "관리자에게 문의하세요."),

    // Auth
    WRONG_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "인증 실패", "잘못된 아이디 또는 비밀번호입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 서명 JWT", "JWT의 비밀키 서명이 올바르지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT 만료", "JWT 토큰이 만료되었습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "JWT 형식 오류", "JWT 형식이 맞지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰 만료", "리프레시 토큰이 만료되었습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 불가", "권한이 없습니다."),

    // Book
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책 없음", "책이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
    private final String reason;

    ErrorCode(HttpStatus status, String message, String reason) {
        this.status = status;
        this.message = message;
        this.reason = reason;
    }
}