package kr.co.road2gm.api.global.response.error.exception;

import kr.co.road2gm.api.global.common.constants.ErrorCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String reason) {
        super(reason);
        this.errorCode = errorCode;
    }
}
