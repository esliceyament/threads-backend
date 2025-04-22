package com.threads.feedservice.service;

import com.threads.feedservice.dto.FeedItemDto;
import com.threads.feedservice.dto.PageDto;
import org.springframework.stereotype.Service;

@Service
public interface FeedService {
    PageDto<FeedItemDto> getUserFeed(String authorizationHeader, int page);
}
