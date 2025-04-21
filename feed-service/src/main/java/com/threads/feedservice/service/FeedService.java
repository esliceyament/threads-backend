package com.threads.feedservice.service;

import com.threads.events.CreatePostEvent;
import com.threads.feedservice.dto.FeedItemDto;
import com.threads.feedservice.dto.PageDto;
import com.threads.feedservice.entity.FeedItem;
import com.threads.feedservice.feign.SecurityFeignClient;
import com.threads.feedservice.mapper.FeedMapper;
import com.threads.feedservice.repository.FeedRepository;
import com.threads.feedservice.service.cache.FeedCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository repository;
    private final SecurityFeignClient securityFeignClient;
    private final FeedMapper mapper;
    private final FeedCacheService cacheService;

    public PageDto<FeedItemDto> getUserFeed(String authorizationHeader, int page, int size) {
        Long currentUserId = getUserId(authorizationHeader);
        PageDto<FeedItemDto> feedPageCached = cacheService.getCachedFeed(currentUserId, page, size);
        if (feedPageCached != null) {
            return feedPageCached;
        }
        PageDto<FeedItemDto> feedPage = getUserFeedOrCache(currentUserId, page, size);
        cacheService.cacheFeed(currentUserId, page, size, feedPage);
        return feedPage;
    }

    private PageDto<FeedItemDto> getUserFeedOrCache(Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FeedItem> feedPage = repository.findAllByUserId(currentUserId, pageable);
        List<FeedItemDto> content = feedPage.stream()
                .map(mapper::toDto).toList();
        return new PageDto<>(page, size, feedPage.getTotalElements(), feedPage.getTotalPages(), content);
    }

    public void handleCreatePostEvent(CreatePostEvent event, String authorizationHeader) {

    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
