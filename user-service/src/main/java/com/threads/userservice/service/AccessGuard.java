package com.threads.userservice.service;

import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.exception.NotFoundException;
import com.threads.userservice.feign.SecurityFeignClient;
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
    private final SecurityFeignClient securityFeignClient;

    public boolean checkAccessToProfile(String authorizationHeader, Long ownerId) {
        UserProfile owner = userProfileRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("No such user " + ownerId));
        if (blockRepository.existsByBlockerIdAndBlockedId(ownerId, getUserId(authorizationHeader))) {
            return false;
        }

        if (owner.getIsPrivate()) {
            return followRepository.existsByFollowerIdAndFollowingId(getUserId(authorizationHeader), ownerId);
        }
        return true;
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
