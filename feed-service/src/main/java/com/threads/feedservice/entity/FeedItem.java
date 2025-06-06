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

    @Column(nullable = false)
    private String authorUsername;

    @Column(nullable = false)
    private String content;

    private String topic;

    private LocalDateTime createdAt;
    private LocalDateTime feedCreatedAt;

    private Boolean isRepost;
    private Long originalPostId;
    private Long repostedByUserId;
    private String repostedByUsername;

    @ElementCollection
    private List<String> mediaUrls;

    private Boolean isVisible;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;

    private double trendScore;
}
