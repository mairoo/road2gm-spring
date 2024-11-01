package kr.co.road2gm.api.global.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// 성공과 실패 타입을 분리하여 Optional 처리 시 타입 호환 문제 해결
// .map()과 .orElseGet()이 같은 타입을 반환해야 하는데, 구체 클래스로는 타입 불일치 발생

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ApiResponse<T> {
    @JsonProperty("status")
    private int status;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ApiResponse(T data,
                       int status,
                       String message) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, 200, "OK");
    }

    public static <T> ApiResponse<T> of(T data, HttpStatus status, String message) {
        return new ApiResponse<>(data, status.value(), message);
    }
}