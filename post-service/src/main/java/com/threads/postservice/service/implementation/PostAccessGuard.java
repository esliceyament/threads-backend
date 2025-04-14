package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.feign.UserFeignClient;
import com.threads.request.AccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAccessGuard {
    private final UserFeignClient userFeignClient;
    ////can like
    ////can reply
    ////can repost
    ////can quote

    public void checkOwnership(Post post, Long currentUserId) {
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new RuntimeException("");
        }
    }

    public void checkPostVisible(Post post, String message) {
        if (post.getHidden()) {
            throw new RuntimeException(message);
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
}
