package com.threads.postservice.entity;

import com.threads.postservice.enums.ReplyPermission;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;
    private String content;
    private String topic;

    private Boolean isPost;
    private Boolean isReply;

    private Long originalPostId;
    private Long replyToPostId;

    private Long pinnedReplyId;

    private Boolean hidden;

    @Enumerated(EnumType.STRING)
    private ReplyPermission replyPermission;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;
    private int reportCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "postId", referencedColumnName = "id")
    private List<PostMedia> media;
}
