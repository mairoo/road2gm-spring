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
    // - 지정된 생성자 파라미터만 포함하는 빌더 생성
    // - 특정 필드만 선택적으로 빌더에 포함 가능
    // - 여러 개의 빌더 패턴 구현 가능
    // - 생성자의 유효성 검증 로직 활용 가능
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
        // 여기에서 생성자 레벨 빌더 사용
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
    // 클래스 레벨: @Builder
    // - 클래스의 모든 필드를 포함하는 빌더 생성 (선택적으로 필드를 제외할 수 없음)
    // - 기본 생성자가 private으로 생성 (다른 생성자를 사용할 수 없고 빌더를 이용한 객체 생성)
    // - static builder() 메소드 생성
    // - 모든 필드에 대한 setter 메소드 생성
    // - build() 메소드 생성
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
