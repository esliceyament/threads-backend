package com.threads.userservice.service;

import com.threads.userservice.dto.UserProfileDto;
import com.threads.userservice.entity.Block;
import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.exception.AlreadyFollowingException;
import com.threads.userservice.exception.InvalidFollowRequestException;
import com.threads.userservice.exception.NotFoundException;
import com.threads.userservice.feign.SecurityFeignClient;
import com.threads.userservice.mapper.UserProfileMapper;
import com.threads.userservice.repository.BlockRepository;
import com.threads.userservice.repository.FollowRepository;
import com.threads.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final UserProfileRepository userProfileRepository;
    private final SecurityFeignClient securityFeignClient;
    private final UserProfileMapper mapper;
    private final FollowRepository followRepository;

    @Transactional
    public void blockUser(Long id, String authorizationHeader) {
        if (getUserId(authorizationHeader).equals(id)) {
            throw new InvalidFollowRequestException("Cannot block yourself!");
        }
        UserProfile blocked = userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such user " + id));
        UserProfile blocker = userProfileRepository.findById(getUserId(authorizationHeader)).get();
        if (blockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), id)) {
            throw new AlreadyFollowingException("You already blocked user " + id);
        }
        followRepository.deleteByFollowerIdAndFollowingId(blocked.getId(), blocker.getId());
        followRepository.deleteByFollowerIdAndFollowingId(blocker.getId(), blocked.getId());
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        blockRepository.save(block);
    }

    public void unblock(Long id, String authorizationHeader) {
        if (getUserId(authorizationHeader).equals(id)) {
            throw new InvalidFollowRequestException("Cannot unblock yourself!");
        }
        UserProfile blocked = userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such user " + id));
        UserProfile blocker = userProfileRepository.findById(getUserId(authorizationHeader)).get();

        Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
                        .orElseThrow(() -> new NotFoundException("Did not blocked uesr " + id));
        blockRepository.delete(block);
    }

    public boolean isBlocked(Long blocker, Long blocked) {
        return blockRepository.existsByBlockerIdAndBlockedId(blocker, blocked);
    }

    public List<UserProfileDto> getBlockedUsers(String authorizationHeader) {
        return blockRepository.findByBlockerId(getUserId(authorizationHeader)).stream()
                .map(Block::getBlocked)
                .map(mapper::toDto).toList();
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
