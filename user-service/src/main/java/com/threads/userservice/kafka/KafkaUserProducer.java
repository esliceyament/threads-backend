package com.threads.userservice.kafka;

import com.threads.events.UsernameUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUserProducer {
    private final KafkaTemplate<String, UsernameUpdateEvent> kafkaTemplate;

    public void sendUsernameUpdateEvent(UsernameUpdateEvent usernameUpdateEvent) {
        kafkaTemplate.send("username-update-events", usernameUpdateEvent);
    }
}
