package com.threads.postservice.dto;

import com.threads.postservice.enums.ReplyPermission;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostUpdateDto {
    @Size(max = 500)
    private String content;
    @Size(max = 20)
    private String topic;

    private ReplyPermission replyPermission;
}
