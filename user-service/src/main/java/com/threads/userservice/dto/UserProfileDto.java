package com.threads.userservice.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;

    private String username;

    private String bio;

    private Boolean isPrivate;

}
