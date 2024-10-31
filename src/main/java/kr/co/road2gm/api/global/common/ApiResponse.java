package kr.co.road2gm.api.global.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.road2gm.api.global.error.ErrorResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

// 성공과 실패 타입을 분리하여 Optional 처리 시 타입 호환 문제 해결
// .map()과 .orElseGet()이 같은 타입을 반환해야 하는데, 구체 클래스로는 타입 불일치 발생

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ApiResponse<T, E> {
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

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private E error;

    public ApiResponse(T data,
                       E error,
                       int status,
                       String message) {
        this.data = data;
        this.error = error;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T, E> ApiResponse<T, E> success(T data) {
        return new ApiResponse<>(data, null, 200, "OK");
    }

    public static <T, E> ApiResponse<T, E> success(T data, String message) {
        return new ApiResponse<>(data, null, 200, message);
    }

    public static <T, E> ApiResponse<T, E> error(E error) {
        if (error instanceof ErrorResponse errorResponse) {
            return new ApiResponse<>(null, error, errorResponse.getStatus(), errorResponse.getMessage());
        }

        return new ApiResponse<>(null, error, 500, null);
    }
}