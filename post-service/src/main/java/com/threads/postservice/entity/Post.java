package com.threads.postservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;
    private String content;

    private boolean isPost;
    private boolean isReply;

    private UUID originalPostId;
    private UUID replyToPostId;

    private boolean isPrivateReply;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;
}
