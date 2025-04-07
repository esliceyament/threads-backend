package com.threads.userservice.kafka;

import com.threads.UserCreatedEvent;
import com.threads.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreateListener {
    private final UserProfileService service;

    @KafkaListener(topics = "user-events", groupId = "user-service")
    public void handleUserCreateEvent(UserCreatedEvent userCreatedEvent) {
        service.createProfile(userCreatedEvent);
    }
}
