package com.threads;

public class PinPostEvent {
    private Long currentUserId;
    private Long pinnedPostId;

    public PinPostEvent() {}

    public PinPostEvent(Long currentUserId, Long pinnedPostId) {
        this.currentUserId = currentUserId;
        this.pinnedPostId = pinnedPostId;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }
    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Long getPinnedPostId() {
        return pinnedPostId;
    }
    public void setPinnedPostId(Long pinnedPostId) {
        this.pinnedPostId = pinnedPostId;
    }
}
