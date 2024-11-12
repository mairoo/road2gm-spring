package kr.co.road2gm.api.global.response.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    @JsonProperty("status")
    private final int status;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("path")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String path;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("errors")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldError> errors;

    @Builder
    // - 지정된 생성자 파라미터만 포함하는 빌더 생성
    // - 특정 필드만 선택적으로 빌더에 포함 가능
    // - 여러 개의 빌더 패턴 구현 가능
    // - 생성자의 유효성 검증 로직 활용 가능
    public ErrorResponse(HttpStatus status,
                         String path,
                         String message,
                         List<FieldError> errors) {
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse of(HttpStatus status,
                                   String path,
                                   String message) {
        return ErrorResponse.builder()
                .status(status)
                .path(path)
                .message(message)
                .errors(List.of())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode,
                                   String path) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .path(path)
                .message(errorCode.getMessage())
                .errors(List.of())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode,
                                   String path,
                                   BindingResult bindingResult) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .path(path)
                .message(errorCode.getMessage())
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
