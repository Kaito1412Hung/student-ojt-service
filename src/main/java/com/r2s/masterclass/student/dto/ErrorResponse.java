package com.r2s.masterclass.student.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Định dạng lỗi chuẩn trả về cho client: { timestamp, status, message, errors }.
 * Trường errors chỉ xuất hiện khi có lỗi validation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        Map<String, String> errors
) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(Instant.now(), status, message, null);
    }

    public static ErrorResponse of(int status, String message, Map<String, String> errors) {
        return new ErrorResponse(Instant.now(), status, message, errors);
    }
}
