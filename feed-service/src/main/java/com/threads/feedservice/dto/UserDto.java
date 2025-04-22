package com.threads.feedservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String username;
    List<Long> followers;
}
