package com.r2s.masterclass.student.exception;

import com.r2s.masterclass.student.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bắt lỗi tập trung, trả về JSON chuẩn { timestamp, status, message, errors }.
 * Chi tiết kỹ thuật (SQL, stacktrace) chỉ ghi vào log server, không bao giờ gửi cho client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 400 - Dữ liệu không qua được Jakarta Validation. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.putIfAbsent(err.getField(), err.getDefaultMessage()));
        return ErrorResponse.of(400, "Dữ liệu đầu vào không hợp lệ", errors);
    }

    /** 400 - JSON sai cú pháp hoặc ID không phải UUID. */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(Exception ex) {
        return ErrorResponse.of(400, "Yêu cầu không đúng định dạng");
    }

    /** 404 - Không tìm thấy sinh viên. */
    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(StudentNotFoundException ex) {
        return ErrorResponse.of(404, ex.getMessage());
    }

    /** 404 - Sai đường dẫn API. */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResource(NoResourceFoundException ex) {
        return ErrorResponse.of(404, "Không tìm thấy tài nguyên");
    }

    /** 405 - Gọi sai HTTP method (ví dụ GET vào /register). */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ErrorResponse.of(405, "Phương thức HTTP không được hỗ trợ");
    }

    /** 415 - Thiếu header Content-Type: application/json. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorResponse handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ErrorResponse.of(415, "Content-Type phải là application/json");
    }

    /** 409 - Trùng Email/CCCD do Service phát hiện. */
    @ExceptionHandler(StudentAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(StudentAlreadyExistsException ex) {
        return ErrorResponse.of(409, ex.getMessage());
    }

    /** 409 - Trùng do unique constraint ở DB (2 request song song). Ẩn câu SQL gốc. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Vi phạm ràng buộc dữ liệu", ex);
        return ErrorResponse.of(409, new StudentAlreadyExistsException().getMessage());
    }

    /** 500 - Lỗi truy cập DB khác. Chỉ log, không lộ SQL. */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDataAccess(DataAccessException ex) {
        log.error("Lỗi truy cập cơ sở dữ liệu", ex);
        return ErrorResponse.of(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");
    }

    /** 500 - Mọi lỗi còn lại. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex) {
        log.error("Lỗi không mong muốn", ex);
        return ErrorResponse.of(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");
    }
}
