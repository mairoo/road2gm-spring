package kr.co.road2gm.api.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String message;
    private final String details;
    private final String path;
    private final List<FieldError> errors;

    @Builder
    public ErrorResponse(int status, String message, String details, String path, List<FieldError> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.details = details;
        this.path = path;
        this.errors = errors != null ? errors : List.of();
    }

    // 기본 에러 응답 생성을 위한 정적 팩토리 메서드
    public static ErrorResponse of(int status, String message, String details, String path) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .details(details)
                .path(path)
                .errors(List.of())
                .build();
    }

    // ValidationError가 있는 경우의 응답 생성을 위한 정적 팩토리 메서드
    public static ErrorResponse of(int status, String message, String details, String path, List<FieldError> errors) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .details(details)
                .path(path)
                .errors(errors)
                .build();
    }

    // BindingResult로부터 ErrorResponse를 생성하는 정적 팩토리 메서드
    public static ErrorResponse of(BindingResult bindingResult, HttpStatus status, String path) {
        return ErrorResponse.builder()
                .status(status.value())
                .message("Validation failed")
                .details("Field validation error")
                .path(path)
                .errors(FieldError.of(bindingResult))
                .build();
    }

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        // BindingResult로부터 FieldError 목록 생성
        public static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> FieldError.builder()
                            .field(error.getField())
                            .value(error.getRejectedValue() != null ? error.getRejectedValue().toString() : "")
                            .reason(error.getDefaultMessage())
                            .build())
                    .toList();
        }
    }
}
