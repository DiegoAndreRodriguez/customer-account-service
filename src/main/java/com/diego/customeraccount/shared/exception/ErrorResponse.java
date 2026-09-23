package com.diego.customeraccount.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String ruleCode,
        List<FieldErrorDetail> fieldErrors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path, String ruleCode) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, ruleCode, null);
    }

    public static ErrorResponse validation(String message, String path, List<FieldErrorDetail> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), 400, "BAD_REQUEST", message, path, null, fieldErrors);
    }
}