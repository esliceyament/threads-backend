package com.threads.events;

import java.time.LocalDateTime;
import java.util.List;

public class CreatePostEvent {
    private Long userId;

    private Long postId;
    private Long authorId;
    private String authorUsername;

    private String content;
    private String topic;

    private LocalDateTime createdAt;

    private Boolean isRepost;
    private Long originalPostId;
    private Long repostedByUserId;
    private String repostedByUsername;

    private List<String> mediaUrls;

    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;

    public CreatePostEvent() {}

    public CreatePostEvent(Long userId, Long postId, Long authorId, String authorUsername, String content, String topic,

     LocalDateTime createdAt, Boolean isRepost, Long repostedByUserId, String repostedByUsername, List<String> mediaUrls,

     int likeCount, int repostCount, int replyCount, int sendCount) {
        this.userId = userId;
        this.postId = postId;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.topic = topic;
        this.createdAt = createdAt;
        this.isRepost = isRepost;
        this.repostedByUserId = repostedByUserId;
        this.repostedByUsername = repostedByUsername;
        this.mediaUrls = mediaUrls;
        this.likeCount = likeCount;
        this.repostCount = repostCount;
        this.replyCount = replyCount;
        this.sendCount = sendCount;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsRepost() { return isRepost; }
    public void setIsRepost(Boolean isRepost) { this.isRepost = isRepost; }

    public Long getOriginalPostId() {return originalPostId;}
    public void setOriginalPostId(Long originalPostId) {this.originalPostId = originalPostId;}

    public Long getRepostedByUserId() { return repostedByUserId; }
    public void setRepostedByUserId(Long repostedByUserId) { this.repostedByUserId = repostedByUserId; }

    public String getRepostedByUsername() { return repostedByUsername; }
    public void setRepostedByUsername(String repostedByUsername) { this.repostedByUsername = repostedByUsername; }

    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getRepostCount() { return repostCount; }
    public void setRepostCount(int repostCount) { this.repostCount = repostCount; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public int getSendCount() { return sendCount; }
    public void setSendCount(int sendCount) { this.sendCount = sendCount; }

}
