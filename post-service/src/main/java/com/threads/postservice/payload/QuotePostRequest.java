package com.threads.postservice.payload;

import lombok.Data;

@Data
public class QuotePostRequest {
    private String content;
    private String topic;
}
