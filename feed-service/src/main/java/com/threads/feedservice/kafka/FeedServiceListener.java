package com.threads.feedservice.kafka;

import com.threads.events.CreatePostEvent;
import com.threads.feedservice.repository.FeedRepository;
import com.threads.feedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceListener {
    private final FeedRepository repository;
    private final FeedService service;

    @KafkaListener
    public void consume(CreatePostEvent event) {

    }

}
