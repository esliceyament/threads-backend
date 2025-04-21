package com.threads.userservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserFeedDto {
    private String username;
    private String avatarUrl;
    List<Long> followers;
}
