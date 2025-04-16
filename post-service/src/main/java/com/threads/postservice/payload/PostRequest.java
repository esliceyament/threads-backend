package com.threads.postservice.payload;

import com.threads.postservice.enums.ReplyPermission;
import lombok.Data;

@Data
public class PostRequest {
    private String content;
    private String topic;

    private ReplyPermission replyPermission = ReplyPermission.EVERYONE;
}
