package com.threads.events;

public class PostStatusEvent {
    private long postId;
    private int likeCount;
    private int repostCount;
    private int replyCount;
    private int sendCount;

    public PostStatusEvent(long postId, int likeCount, int repostCount, int replyCount, int sendCount) {
        this.postId = postId;
        this.likeCount = likeCount;
        this.repostCount = repostCount;
        this.replyCount = replyCount;
        this.sendCount = sendCount;
    }

    public PostStatusEvent () {}


    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(int repostCount) {
        this.repostCount = repostCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

}
