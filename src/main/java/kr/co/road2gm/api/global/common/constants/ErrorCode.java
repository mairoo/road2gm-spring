package kr.co.road2gm.api.global.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "올바르지 않은 입력값"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "내부 서버 오류"),

    // Entity
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "엔티티 없음"),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "E002", "이미 존재하는 엔티티"),
    INVALID_ENTITY_STATUS(HttpStatus.BAD_REQUEST, "E003", "잘못된 엔티티 상태"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증실패"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "잘못된 액세스 토큰"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A003", "접근불가"),

    // Business
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "B001", "Insufficient Balance"),
    EXPIRED_DATA(HttpStatus.BAD_REQUEST, "B002", "Data Has Expired");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}