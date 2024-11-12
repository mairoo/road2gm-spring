package kr.co.road2gm.api.global.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "필드 입력값 양식이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청 경로가 올바르지 않습니다."),
    MESSAGE_BODY_NOT_FOUND(HttpStatus.BAD_REQUEST, "HTTP 요청 본문이 없거나 본문 형식이 올바르지 않습니다."),
    JSON_SERIALIZATION_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "응답 객체를 JSON 직렬화하지 못했습니다"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다."),

    // 인증
    WRONG_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 이메일 또는 비밀번호입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "로그인 토큰의 비밀키 서명이 올바르지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인 토큰이 만료되었습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "로그인 토큰 형식이 올바르지 않습니다."),
    REFRESH_TOKEN_COOKIE_NOT_EXIST(HttpStatus.UNAUTHORIZED, "로그인 유지 토큰 쿠키가 없습니다."),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "로그인 유지 토큰이 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인 유지 토큰이 만료되었습니다."),
    FAILED_TO_REFRESH(HttpStatus.UNAUTHORIZED, "로그인 유지 실패했습니다."),

    // OAuth2
    INVALID_OAUTH2_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 소셜로그인 연동 토큰입니다."),
    OAUTH2_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "소셜로그인 연동 토큰이 만료되었습니다."),

    // 인가
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 회원가입
    USERNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "아이디가 이미 존재합니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이메일이 이미 존재합니다."),
    FAILED_TO_CREATE_USER(HttpStatus.CONFLICT, "사용자를 추가하지 못했습니다."),
    ROLE_NOT_FOUND(HttpStatus.CONFLICT, "역할이 존재하지 않습니다."),

    // Book
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 책이 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}