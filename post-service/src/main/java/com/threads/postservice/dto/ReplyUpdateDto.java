package com.threads.postservice.dto;

import com.threads.postservice.entity.PostMedia;
import lombok.Data;

import java.util.List;

@Data
public class ReplyUpdateDto {
    private String content;
    private String topic;

    private List<PostMedia> media;
}
