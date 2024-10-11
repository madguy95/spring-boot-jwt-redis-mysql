package com.springjwt.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ApiResponseFactory {

    private static MessageSource messageSource;

    public ApiResponseFactory(MessageSource messageSource) {
        ApiResponseFactory.messageSource = messageSource;
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> response = new ApiResponse<>(HttpStatus.OK, null, null, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, Object ...args) {
        String messageCode = null;
        String message = null;
        Object[] messageArgs = {};
        if (args.length >= 1) {
            messageCode = (String) args[0];
            messageArgs = Arrays.copyOfRange(args, 1, args.length);;
        }
        if (!StringUtils.isEmpty(messageCode)) {
            message = messageSource.getMessage(messageCode, messageArgs, messageCode, LocaleContextHolder.getLocale());
        }
        ApiResponse<T> response = new ApiResponse<>(status, message, messageCode, null);
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<PagedApiResponse<T>> pagedResponse(
            List<T> data, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        PagedApiResponse<T> pagedResponse = new PagedApiResponse<>(HttpStatus.OK, data, page, size, totalElements, totalPages);
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }
}