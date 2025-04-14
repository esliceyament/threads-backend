package com.threads.postservice.dto;

import com.threads.postservice.entity.PostMedia;
import com.threads.postservice.enums.ReplyPermission;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateDto {
    private String content;
    private String topic;

    private ReplyPermission replyPermission;

    private List<PostMedia> media;
}
