package com.threads.postservice.dto;

import com.threads.postservice.entity.PostMedia;
import com.threads.postservice.enums.ReplyPermission;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;

    private Long authorId;
    @Size(max = 500)
    private String content;
    @Size(max = 20)
    private String topic;

    private Boolean isPost;
    private Boolean isReply;

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
