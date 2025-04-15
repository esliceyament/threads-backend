package com.threads.postservice.dto;

import com.threads.postservice.entity.PostMedia;
import com.threads.postservice.enums.ReplyPermission;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;

    private Long authorId;
    private String content;
    private String topic;

    private boolean isPost;
    private boolean isReply;

    private Long originalPostId;
    private Long replyToPostId;

    private Long pinnedReplyId;

    private Boolean hidden;

    private ReplyPermission replyPermission;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PostMedia> media;
}
