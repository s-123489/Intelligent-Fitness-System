package com.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;

    public ApiResponse(String message) {
        this.message = message;
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message);
    }
}
