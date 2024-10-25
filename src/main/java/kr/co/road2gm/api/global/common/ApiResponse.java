package kr.co.road2gm.api.global.common;

import lombok.Getter;

@Getter
public record ApiResponse<T>(String status, String message, T data) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("성공", "정상적으로 처리되었습니다.", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("에러", message, null);
    }
}