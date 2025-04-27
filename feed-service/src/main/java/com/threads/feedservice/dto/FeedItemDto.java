package com.threads.feedservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedItemDto {

    private Long postId;

    private Long authorId;
    private String authorUsername;

    private String content;
    private String topic;
    private List<String> mediaUrls;

    private LocalDateTime createdAt;

    private Boolean isRepost;
    private Long originalPostId;
    private Long repostedByUserId;
    private String repostedByUsername;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;

    private boolean isRecommended;
}
