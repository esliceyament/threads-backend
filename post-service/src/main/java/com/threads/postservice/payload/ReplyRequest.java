package com.threads.postservice.payload;

import lombok.Data;

@Data
public class ReplyRequest {
    private String content;
    private String topic;
}
