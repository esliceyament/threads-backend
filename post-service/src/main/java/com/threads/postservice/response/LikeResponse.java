package com.threads.postservice.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeResponse {
    private Long userId;
    private LocalDateTime likedAt;
}
