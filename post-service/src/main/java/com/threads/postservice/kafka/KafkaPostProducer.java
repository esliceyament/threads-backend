package com.threads.postservice.kafka;

import com.threads.PinPostEvent;
import com.threads.events.CreatePostEvent;
import com.threads.events.PostStatusEvent;
import com.threads.events.UpdatePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, PinPostEvent> kafkaTemplate;
    private final KafkaTemplate<String, CreatePostEvent> createPostKafkaTemplate;
    private final KafkaTemplate<String, UpdatePostEvent> updatePostKafkaTemplate;
    private final KafkaTemplate<String, PostStatusEvent> postStatusKafkaTemplate;
    private final KafkaTemplate<String, Long> postDeleteKafkaTemplate;
    private final KafkaTemplate<String, Long> postArchiveKafkaTemplate;
    private final KafkaTemplate<String, Long> postUnarchiveKafkaTemplate;

    public void sendPinPostEvent(PinPostEvent pinPostEvent) {
        kafkaTemplate.send("user-post-events", pinPostEvent);
    }

    public void sendCreatePostEvent(CreatePostEvent createPostEvent) {
        createPostKafkaTemplate.send("create-post-events", createPostEvent);
    }

    public void sendUpdatePostEvent(UpdatePostEvent updatePostEvent) {
        updatePostKafkaTemplate.send("update-post-events", updatePostEvent);
    }

    public void sendPostStatusEvent(PostStatusEvent postStatusEvent) {
        postStatusKafkaTemplate.send("post-status-events", postStatusEvent);
    }

    public void sendPostDeleteEvent(Long postId) {
        postDeleteKafkaTemplate.send("delete-post-events", postId);
    }

    public void sendPostArchiveEvent(Long postId) {
        postArchiveKafkaTemplate.send("archive-post-events", postId);
    }
    public void sendPostUnarchiveEvent(Long postId) {
        postUnarchiveKafkaTemplate.send("unarchive-post-events", postId);
    }
}
