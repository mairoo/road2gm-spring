package kr.co.road2gm.api.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

@Getter
public class ErrorResponse {
    @JsonProperty("status")
    private final int status;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("path")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String path;

    @JsonProperty("errors")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldError> errors;

    @Builder
    // - 지정된 생성자 파라미터만 포함하는 빌더 생성
    // - 특정 필드만 선택적으로 빌더에 포함 가능
    // - 여러 개의 빌더 패턴 구현 가능
    // - 생성자의 유효성 검증 로직 활용 가능
    public ErrorResponse(HttpStatus status,
                         String title,
                         String message,
                         String path,
                         List<FieldError> errors) {
        this.status = status.value();
        this.title = title;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public static ErrorResponse of(HttpStatus status,
                                   String title,
                                   String message,
                                   String path) {
        return ErrorResponse.builder()
                .status(status)
                .title(title)
                .message(message)
                .path(path)
                .errors(List.of())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode,
                                   String path) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .title(errorCode.getTitle())
                .message(errorCode.getMessage())
                .path(path)
                .errors(List.of())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode,
                                   String path,
                                   BindingResult bindingResult) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .title(errorCode.getTitle())
                .message(errorCode.getMessage())
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
