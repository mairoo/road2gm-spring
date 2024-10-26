package kr.co.road2gm.api.global.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ApiResponse<T>(String message, T data) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>("Success", data);
    }

    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(message, data);
    }
}