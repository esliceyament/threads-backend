package com.threads.postservice.kafka;

import com.threads.PinPostEvent;
import com.threads.events.CreatePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinnedPostProducer {
    private final KafkaTemplate<String, PinPostEvent> kafkaTemplate;
    private final KafkaTemplate<String, CreatePostEvent> createPostKafkaTemplate;

    public void sendPinPostEvent(PinPostEvent pinPostEvent) {
        kafkaTemplate.send("user-post-events", pinPostEvent);
    }

    public void sendCreatePostEvent(CreatePostEvent createPostEvent) {
        createPostKafkaTemplate.send("create-post-events", createPostEvent);
    }
}
