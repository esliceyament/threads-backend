package com.threads.feedservice.kafka;

import com.threads.events.CreatePostEvent;
import com.threads.feedservice.repository.FeedRepository;
import com.threads.feedservice.service.implementation.FeedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceListener {
    private final FeedRepository repository;
    private final FeedServiceImpl service;

    @KafkaListener
    public void consume(CreatePostEvent event) {

    }

}
