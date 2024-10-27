package kr.co.road2gm.api.global.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid Input Value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "Internal Server Error"),

    // Entity
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "Entity Not Found"),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "E002", "Entity Already Exists"),
    INVALID_ENTITY_STATUS(HttpStatus.BAD_REQUEST, "E003", "Invalid Entity Status"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Unauthorized"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "Access Denied"),

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