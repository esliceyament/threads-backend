package com.threads.postservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReplyUpdateDto {
    @Size(max = 500)
    private String content;
    @Size(max = 20)
    private String topic;
}
