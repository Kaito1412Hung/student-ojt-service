package com.r2s.masterclass.student.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Ném ra khi Email hoặc CCCD đã tồn tại -> 409 Conflict. */
@ResponseStatus(HttpStatus.CONFLICT)
public class StudentAlreadyExistsException extends RuntimeException {

    public StudentAlreadyExistsException() {
        super("Số CCCD hoặc Email đã tồn tại trên hệ thống OJT");
    }
}
