package com.threads.authservice.kafka;

import com.threads.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreateProducer {
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        kafkaTemplate.send("user-events", userCreatedEvent);
    }
}
