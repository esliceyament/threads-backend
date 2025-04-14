package com.threads.postservice.payload;

import com.threads.postservice.entity.PostMedia;
import lombok.Data;

import java.util.List;

@Data
public class ReplyRequest {
    private String content;
    private String topic;

    private List<PostMedia> media;
}
