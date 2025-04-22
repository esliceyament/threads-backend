package com.threads.userservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserFeedDto {
    private String username;
    List<Long> followers;
}
