package com.springjwt.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.springjwt.constants.MsgConstants.UNEXPECTED_ERROR;

@ControllerAdvice
public class ResponseExceptionHandler
        extends ResponseEntityExceptionHandler {


    private final MessageSource messageSource;

    public ResponseExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        // Log the exception if necessary
        // logger.error("Bad credentials: ", ex);

        return ApiResponseFactory.error(HttpStatus.UNAUTHORIZED, "Invalid username or password. Please try again.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ValidationError> validationErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            validationErrors.add(new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        });
        ApiResponse<Object> response = new ApiResponse<>((HttpStatus) status, null, null, validationErrors);
        return new ResponseEntity<>(response, (HttpStatus) status);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<?> handleAccessDeniedException(
            Exception ex, WebRequest request) {
        return ApiResponseFactory.error(HttpStatus.FORBIDDEN, "Access denied message here");
    }

    @ExceptionHandler(value
            = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value
            = {ValidationException.class})
    protected ResponseEntity<Object> handleBaseException(ValidationException ex, Locale locale) {
        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(ex.getError());
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, null, null, validationErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value
            = {AppException.class})
    protected ResponseEntity<Object> handleBaseException(AppException baseException, Locale locale) {
        String messageName = baseException.getMessage();
        Object[] args = baseException.getArgs();
        String messageDesc = messageSource.getMessage(messageName, args, locale);
        baseException.setMessageDesc(messageDesc);
        return new ResponseEntity<>(baseException, baseException.getStatusCode());
    }

    @ExceptionHandler(value
            = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception ex, Locale locale) {
        String messageDesc = messageSource.getMessage(UNEXPECTED_ERROR, new Object[]{}, locale);
        AppException baseException = new AppException();
        baseException.setMessage(UNEXPECTED_ERROR);
        baseException.setMessageDesc(messageDesc);
        baseException.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(baseException, baseException.getStatusCode());
    }
}
