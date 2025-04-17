package com.threads.userservice.service;

import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.exception.AccessDeniedException;
import com.threads.userservice.exception.NotFoundException;
import com.threads.userservice.repository.BlockRepository;
import com.threads.userservice.repository.FollowRepository;
import com.threads.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessGuard {
    private final BlockRepository blockRepository;
    private final FollowRepository followRepository;
    private final UserProfileRepository userProfileRepository;

    public void checkAccessToProfile(Long currentUserId, Long ownerId) {
        if (currentUserId.equals(ownerId)) {
            return;
        }
        UserProfile owner = userProfileRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("No such user " + ownerId));
        if (blockRepository.existsByBlockerIdAndBlockedId(ownerId, currentUserId)) {
            throw new AccessDeniedException("You are not allowed to see profile!");
        }

        if (owner.getIsPrivate() && !followRepository.existsByFollowerIdAndFollowingId(currentUserId, ownerId)) {
                throw new AccessDeniedException("You are not allowed to see profile!");
            }
        }

    public void checkFollower(Long currentUserId, Long ownerId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(currentUserId, ownerId)) {
            throw new AccessDeniedException("You don't follow this person!");
        }
    }

    public void canSendPost(Long currentUserId, Long ownerId) {
        if (blockRepository.existsByBlockerIdAndBlockedId(ownerId, currentUserId)) {
            throw new AccessDeniedException("You are not allowed to see profile!");
        }
    }

    public boolean checkUserExists(Long userId) {
        return userProfileRepository.existsById(userId);
    }
}
