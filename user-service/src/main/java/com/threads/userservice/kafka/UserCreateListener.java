package com.threads.userservice.kafka;

import com.threads.PinPostEvent;
import com.threads.UserCreatedEvent;
import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.exception.NotFoundException;
import com.threads.userservice.repository.UserProfileRepository;
import com.threads.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreateListener {
    private final UserProfileService service;
    private final UserProfileRepository repository;

    @KafkaListener(topics = "user-events", groupId = "user-service")
    public void handleUserCreateEvent(UserCreatedEvent userCreatedEvent) {
        service.createProfile(userCreatedEvent);
    }

    @KafkaListener(topics = "user-post-events", groupId = "user-service")
    public void handlePinPostEvent(PinPostEvent pinPostEvent) {
        UserProfile userProfile = repository.findById(pinPostEvent.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("User not found!"));
        userProfile.setPinnedPostId(pinPostEvent.getPinnedPostId());
        repository.save(userProfile);
    }
}
