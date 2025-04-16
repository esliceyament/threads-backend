package com.threads.postservice.dto;

import com.threads.postservice.enums.ReplyPermission;
import lombok.Data;

@Data
public class PostUpdateDto {
    private String content;
    private String topic;

    private ReplyPermission replyPermission;
}
