package com.threads.postservice.kafka;

import com.threads.PinPostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinnedPostProducer {
    private final KafkaTemplate<String, PinPostEvent> kafkaTemplate;

    public void sendPinPostEvent(PinPostEvent pinPostEvent) {
        kafkaTemplate.send("user-post-events", pinPostEvent);
    }
}
