package com.springjwt.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedApiResponse<T> {
    private HttpStatus status;
    private List<T> data; // Dữ liệu phân trang
    private int page; // Số trang hiện tại
    private int size; // Kích thước trang
    private long totalElements; // Tổng số mục
    private int totalPages; // Tổng số trang
}

