package com.threads.postservice.payload;

import com.threads.postservice.enums.ReplyPermission;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostRequest {
    private String content;
    private String topic;

    private ReplyPermission replyPermission = ReplyPermission.EVERYONE;

    private List<MultipartFile> media;
}
