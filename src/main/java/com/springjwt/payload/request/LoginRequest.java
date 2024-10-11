package com.springjwt.payload.request;


import com.springjwt.annotations.StrProc;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    @StrProc
    private String password;

}
