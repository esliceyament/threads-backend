package com.threads.postservice.response;

import lombok.Data;

@Data
public class RepostResponse {
    private Long userId;
    private PostResponse post;
}
