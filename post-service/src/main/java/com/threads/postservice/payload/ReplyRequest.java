package com.threads.postservice.payload;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReplyRequest {
    @Size(max = 500)
    private String content;
    @Size(max = 20)
    private String topic;
}
