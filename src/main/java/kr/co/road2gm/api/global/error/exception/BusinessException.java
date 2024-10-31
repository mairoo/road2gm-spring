package kr.co.road2gm.api.global.error.exception;

import kr.co.road2gm.api.global.common.constants.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getReason());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String reason) {
        super(reason);
        this.errorCode = errorCode;
    }
}
