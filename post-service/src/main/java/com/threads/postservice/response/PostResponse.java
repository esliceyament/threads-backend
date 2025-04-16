package com.threads.postservice.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private Long authorId;
    private String content;
    private String topic;

    private Long originalPostId;
    private Long replyToPostId;

    private Long pinnedReplyId;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;

    private LocalDateTime createdAt;

    private List<PostMediaResponse> media;
}
