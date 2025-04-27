package com.threads.userservice.service;

import com.threads.UserCreatedEvent;
import com.threads.events.UsernameUpdateEvent;
import com.threads.userservice.dto.UserFeedDto;
import com.threads.userservice.dto.UserProfileDto;
import com.threads.userservice.entity.Follow;
import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.exception.AlreadyExistsException;
import com.threads.userservice.exception.NotFoundException;
import com.threads.userservice.feign.SecurityFeignClient;
import com.threads.userservice.kafka.KafkaUserProducer;
import com.threads.userservice.mapper.UserProfileMapper;
import com.threads.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;
    private final SecurityFeignClient securityFeignClient;
    private final AccessGuard accessGuard;
    private final KafkaUserProducer producer;

    public void createProfile(UserCreatedEvent request) {
        UserProfile profile = new UserProfile();
        profile.setId(request.getUserId());
        profile.setUsername(request.getUsername());
        profile.setIsPrivate(false);
        repository.save(profile);
    }

    public UserProfileDto updateProfile(String authorizationHeader, UserProfileDto dto) {
        Long currentUserId = getUserId(authorizationHeader);
        UserProfile userProfile = repository.findById(currentUserId).get();
        if (repository.existsByUsername(dto.getUsername()) && !userProfile.getUsername().equals(dto.getUsername())) {
            throw new AlreadyExistsException("Username already exists!");
        }
        mapper.updateProfile(dto, userProfile);
        repository.save(userProfile);
        if (dto.getUsername() != null) {
            UsernameUpdateEvent event = new UsernameUpdateEvent(currentUserId, dto.getUsername());
            producer.sendUsernameUpdateEvent(event);
        }
        return mapper.toDto(userProfile);
    }

    public List<UserProfileDto> searchProfile(String username) {
        return repository.searchProfiles(username).stream()
                .map(mapper::toDto).toList();
    }

    public UserProfileDto getProfile(Long id, String authorizationHeader) {
        UserProfile user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        accessGuard.checkAccessToProfile(currentUserId, id);
        return mapper.toDto(user);
    }

    public UserProfileDto getMyProfile(String authorizationHeader) {
        return mapper.toDto(repository.findById(getUserId(authorizationHeader)).get());
    }

    public UserFeedDto getProfile(Long id) {
        UserProfile user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found!"));
        Set<Follow> followList = user.getFollowers();
        UserFeedDto userFeedDto = new UserFeedDto();
        userFeedDto.setUsername(user.getUsername());
        userFeedDto.setFollowers(followList.stream().map(follow -> follow.getFollower().getId()).toList());
        return userFeedDto;
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
