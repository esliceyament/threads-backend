package com.threads.feedservice.kafka;

import com.threads.events.CreatePostEvent;
import com.threads.events.PostStatusEvent;
import com.threads.events.UpdatePostEvent;
import com.threads.events.UsernameUpdateEvent;
import com.threads.feedservice.service.implementation.FeedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceListener {
    private final FeedServiceImpl service;

    @KafkaListener(topics = "create-post-events", groupId = "feed-service")
    public void consumeCreatePost(CreatePostEvent event) {
        service.handleCreatePostEvent(event);
    }

    @KafkaListener(topics = "update-post-events", groupId = "feed-service")
    public void consumeUpdatePost(UpdatePostEvent updatePostEvent) {
        service.handleUpdatePostEvent(updatePostEvent);
    }

    @KafkaListener(topics = "post-status-events", groupId = "feed-service")
    public void consumePostStatus(PostStatusEvent postStatusEvent) {
        service.handlePostStatusEvent(postStatusEvent);
    }

    @KafkaListener(topics = "username-update-events", groupId = "feed-service")
    public void consumeUsernameUpdate(UsernameUpdateEvent usernameUpdateEvent) {
        service.handleUsernameUpdateEvent(usernameUpdateEvent);
    }

    @KafkaListener(topics = "delete-post-events", groupId = "feed-service")
    public void consumePostDelete(Long postId) {
        service.handlePostDeleteEvent(postId);
    }

    @KafkaListener(topics = "delete-repost-events", groupId = "feed-service")
    public void consumeRepostDelete(Long postId) {
        service.handleRepostDeleteEvent(postId);
    }

    @KafkaListener(topics = "archive-post-events", groupId = "feed-service")
    public void consumePostArchive(Long postId) {
        service.handlePostArchiveEvent(postId);
    }

    @KafkaListener(topics = "unarchive-post-events", groupId = "feed-service")
    public void consumePostUnarchive(Long postId) {
        service.handlePostUnarchiveEvent(postId);
    }
}
