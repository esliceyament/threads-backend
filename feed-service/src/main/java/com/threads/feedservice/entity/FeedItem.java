package com.threads.feedservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class FeedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long authorId;
    private String avatarUrl;

    @Column(nullable = false)
    private String authorUsername;

    @Column(nullable = false)
    private String content;

    private String topic;

    private LocalDateTime createdAt;

    private Boolean isRepost;
    private Long repostedByUserId;
    private String repostedByUsername;

    @ElementCollection
    private List<String> mediaUrls;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;
}
