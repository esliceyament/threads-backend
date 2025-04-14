package com.threads.request;

public class AccessRequest {
    private Long currentUserId;
    private Long ownerId;

    public AccessRequest() {}

    public AccessRequest(Long currentUserId, Long ownerId) {
        this.currentUserId = currentUserId;
        this.ownerId = ownerId;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }
    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
