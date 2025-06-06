package com.threads.userservice.payload;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String username;

    private String bio;

    private Boolean isPrivate;
}
