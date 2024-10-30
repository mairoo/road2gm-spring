package kr.co.road2gm.api.global.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final int status;

    private final String title;

    private final String message;

    private final String path;

    private final List<FieldError> errors;

    @Builder
    // - 지정된 생성자 파라미터만 포함하는 빌더 생성
    // - 특정 필드만 선택적으로 빌더에 포함 가능
    // - 여러 개의 빌더 패턴 구현 가능
    // - 생성자의 유효성 검증 로직 활용 가능
    public ErrorResponse(HttpStatus status, String title, String message, String path, List<FieldError> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.title = title;
        this.message = message;
        this.path = path;
        this.errors = errors != null ? errors : List.of();
    }

    // 기본 에러 응답 생성을 위한 정적 팩토리 메서드
    public static ErrorResponse of(HttpStatus status, String title, String message, String path) {
        // 여기에서 생성자 레벨 빌더 사용
        return ErrorResponse.builder()
                .status(status)
                .title(title)
                .message(message)
                .path(path)
                .errors(List.of())
                .build();
    }

    // ValidationError가 있는 경우의 응답 생성을 위한 정적 팩토리 메서드
    public static ErrorResponse of(HttpStatus status, String title, String message, String path,
                                   List<FieldError> errors) {
        return ErrorResponse.builder()
                .status(status)
                .title(title)
                .message(message)
                .path(path)
                .errors(errors)
                .build();
    }

    // BindingResult로부터 ErrorResponse를 생성하는 정적 팩토리 메서드
    public static ErrorResponse of(HttpStatus status, String path, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .status(status)
                .title("입력 필드 검증 실패")
                .message("필드 입력값 양식이 올바르지 않습니다.")
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
