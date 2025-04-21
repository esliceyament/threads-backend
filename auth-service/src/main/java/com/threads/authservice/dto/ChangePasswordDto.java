package com.threads.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @Size(min = 8, max = 20)
    @NotBlank(message = "Enter the password!")
    private String password;
}
