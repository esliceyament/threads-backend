package com.threads.events;

public class NewFollowEvent {
    private Long followerId;
    private Long followingId;
    private String followingUsername;

    public NewFollowEvent() {}

    public NewFollowEvent(Long followerId, Long followingId, String followingUsername) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.followingUsername = followingUsername;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public String getFollowingUsername() {
        return followingUsername;
    }

    public void setFollowingUsername(String followingUsername) {
        this.followingUsername = followingUsername;
    }
}
