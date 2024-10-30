package kr.co.road2gm.api.global.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ApiResponse<T> {
    private final T data;

    private final String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "성공");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, message);
    }
}