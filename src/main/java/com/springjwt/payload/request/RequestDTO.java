package com.springjwt.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class RequestDTO {
    @JsonFormat(pattern = "yyyy/MM/dd")
    public LocalDate customLocalDate;
    //    @JsonFormat(pattern = "yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public LocalDate localDate;
    //    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public LocalDateTime localDateTime;
    //    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public ZonedDateTime zonedDateTime;
}
