package com.threads.userservice.kafka;

import com.threads.events.NewFollowEvent;
import com.threads.events.UsernameUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaUserProducer {
    private final KafkaTemplate<String, UsernameUpdateEvent> kafkaTemplate;
    private final KafkaTemplate<String, NewFollowEvent> newFollowEventKafkaTemplate;

    public void sendUsernameUpdateEvent(UsernameUpdateEvent usernameUpdateEvent) {
        kafkaTemplate.send("username-update-events", usernameUpdateEvent);
    }

    public void sendNewFollowEvent(NewFollowEvent newFollowEvent) {
        newFollowEventKafkaTemplate.send("new-follow-events", newFollowEvent);
    }
}
