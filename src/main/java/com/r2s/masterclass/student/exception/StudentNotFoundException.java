package com.r2s.masterclass.student.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Ném ra khi không tìm thấy sinh viên theo ID -> 404 Not Found. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException() {
        super("Không tìm thấy hồ sơ sinh viên với ID cung cấp");
    }
}
