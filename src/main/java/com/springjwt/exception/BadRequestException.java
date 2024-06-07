package com.springjwt.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class BadRequestException extends AppException {

    public BadRequestException(String message, Object ...args) {
        this.statusCode = HttpStatus.BAD_REQUEST;
        this.message = message;
        this.args = args;
    }
}
