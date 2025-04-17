package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.exception.OwnershipException;
import com.threads.postservice.feign.UserFeignClient;
import com.threads.request.AccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAccessGuard {
    private final UserFeignClient userFeignClient;

    public void checkOwnership(Long authorId, Long currentUserId) {
        if (!authorId.equals(currentUserId)) {
            throw new OwnershipException("You don't have access!");
        }
    }
    public boolean checkOwnershipToDelete(Post post, Long currentUserId) {
        return !post.getAuthorId().equals(currentUserId);
    }

    public void checkPostVisible(Post post) {
        if (post.getHidden()) {
            throw new IllegalStateException("Cannot find post " + post.getId());
        }
    }

    public void checkUserExists(Long userId) {
        if (!userFeignClient.checkUserExists(userId)) {
            throw new NotFoundException("User " + userId + " not found!");
        }
    }

    public void checkFollower(Long currentUserId, Long ownerId) {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setCurrentUserId(currentUserId);
        accessRequest.setOwnerId(ownerId);
        userFeignClient.checkFollower(accessRequest);
    }

    public void checkUserAccessToPost(Long viewerId, Long authorId) {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setCurrentUserId(viewerId);
        accessRequest.setOwnerId(authorId);
        userFeignClient.checkAccessToProfile(accessRequest);
    }
    public void checkCanSendPost(Long viewerId, Long authorId) {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setCurrentUserId(viewerId);
        accessRequest.setOwnerId(authorId);
        userFeignClient.canSendPost(accessRequest);
    }
}
