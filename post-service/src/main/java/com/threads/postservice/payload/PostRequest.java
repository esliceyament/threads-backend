package com.threads.postservice.payload;

import com.threads.postservice.enums.ReplyPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank
    @Size(max = 500)
    private String content;
    @Size(max = 20)
    private String topic;

    private ReplyPermission replyPermission = ReplyPermission.EVERYONE;
}
