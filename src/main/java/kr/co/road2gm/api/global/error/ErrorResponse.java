package kr.co.road2gm.api.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

@Slf4j
public record ErrorResponse(String message, int status, String code, List<FieldError> errors) {
    // 필드 에러가 없는 경우
    public static ErrorResponse of(String message, int status, String code) {
        return new ErrorResponse(message, status, code, Collections.emptyList());
    }

    // 필드 에러가 있는 경우 (validation 실패 등)
    public static ErrorResponse of(String message, int status, String code, List<FieldError> errors) {
        return new ErrorResponse(message, status, code, errors);
    }

    // Validation 에러 변환
    public static ErrorResponse of(BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .toList();

        return new ErrorResponse(
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_INPUT",
                errors
        );
    }

    // 필드 에러 정보
    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}
