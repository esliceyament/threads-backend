package com.threads.userservice.service;

import com.threads.userservice.dto.UserProfileDto;
import com.threads.userservice.entity.Follow;
import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.exception.AccessDeniedException;
import com.threads.userservice.exception.AlreadyFollowingException;
import com.threads.userservice.exception.InvalidFollowRequestException;
import com.threads.userservice.exception.NotFoundException;
import com.threads.userservice.feign.SecurityFeignClient;
import com.threads.userservice.mapper.UserProfileMapper;
import com.threads.userservice.repository.FollowRepository;
import com.threads.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserProfileRepository userRepository;
    private final BlockService blockService;
    private final SecurityFeignClient securityFeignClient;
    private final UserProfileMapper mapper;
    private final AccessGuard accessGuard;

    public void followUser(String authorizationHeader, Long targetId) {
        if (getUserId(authorizationHeader).equals(targetId)) {
            throw new InvalidFollowRequestException("Cannot follow yourself!");
        }
        UserProfile currentUser = userRepository.findById(getUserId(authorizationHeader)).get();
        UserProfile targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("User " + targetId + " not found!"));

        if (blockService.isBlocked(targetId, currentUser.getId()) || blockService.isBlocked(currentUser.getId(), targetId)) {
            throw new AccessDeniedException("Blocked user!");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), targetUser.getId())) {
            throw new AlreadyFollowingException("Already following.");
        }
        Follow follow = new Follow();
        follow.setFollower(currentUser);
        follow.setFollowing(targetUser);

        followRepository.save(follow);
    }

    public void unfollowUser(String authorizationHeader, Long targetId) {
        if (getUserId(authorizationHeader).equals(targetId)) {
            throw new InvalidFollowRequestException("Cannot unfollow yourself!");
        }

        UserProfile currentUser = userRepository.findById(getUserId(authorizationHeader)).get();
        UserProfile targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("User " + targetId + " not found!"));

        Follow follow = followRepository.findByFollowerAndFollowing(currentUser, targetUser)
                        .orElseThrow(() -> new NotFoundException("You are not following " + targetId));
        followRepository.delete(follow);
    }

    public List<UserProfileDto> getFollowers(Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkAccessToProfile(currentUserId, id);
        return followRepository.findByFollowingId(id)
                .stream()
                .map(Follow::getFollower)
                .map(mapper::toDto).toList();
    }
    public List<UserProfileDto> getFollowings(Long id, String authorizationHeader) {
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkAccessToProfile(currentUserId, id);
        return followRepository.findByFollowerId(id)
                .stream()
                .map(Follow::getFollowing)
                .map(mapper::toDto).toList();
    }

    public Follow getFollow(Long id) {
        return followRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
    }


    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }

    public List<UserProfileDto> getMyFollowers(String authorizationHeader) {
        return followRepository.findByFollowingId(getUserId(authorizationHeader))
                .stream()
                .map(Follow::getFollower)
                .map(mapper::toDto).toList();
    }

    public List<UserProfileDto> getMyFollowings(String authorizationHeader) {
        return followRepository.findByFollowerId(getUserId(authorizationHeader))
                .stream()
                .map(Follow::getFollowing)
                .map(mapper::toDto).toList();
    }
}
