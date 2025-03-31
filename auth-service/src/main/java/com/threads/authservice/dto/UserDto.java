package com.threads.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @Email(message = "Enter right format of email")
    @NotBlank(message = "Enter the email!")
    private String email;
    @Size(min = 8, max = 20)
    @NotBlank(message = "Enter the password!")
    private String password;
    @Pattern(regexp = "^\\+994\\d{9}$", message = "Invalid phone format")
    @NotBlank(message = "Enter your phone!")
    private String phone;
}
