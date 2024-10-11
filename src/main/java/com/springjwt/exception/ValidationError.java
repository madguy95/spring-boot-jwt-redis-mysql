package com.springjwt.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {
    private String paramName;
    private String errorMessage;
}
