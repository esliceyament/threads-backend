package com.threads.feedservice.service.implementation;

import com.threads.events.CreatePostEvent;
import com.threads.events.PostStatusEvent;
import com.threads.events.UpdatePostEvent;
import com.threads.events.UsernameUpdateEvent;
import com.threads.feedservice.dto.FeedItemDto;
import com.threads.feedservice.dto.PageDto;
import com.threads.feedservice.dto.UserDto;
import com.threads.feedservice.entity.FeedItem;
import com.threads.feedservice.feign.SecurityFeignClient;
import com.threads.feedservice.feign.UserFeignClient;
import com.threads.feedservice.mapper.FeedMapper;
import com.threads.feedservice.repository.FeedRepository;
import com.threads.feedservice.service.FeedService;
import com.threads.feedservice.service.cache.FeedCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedRepository repository;
    private final SecurityFeignClient securityFeignClient;
    private final UserFeignClient userFeignClient;
    private final FeedMapper mapper;
    private final FeedCacheService cacheService;

    public PageDto<FeedItemDto> getUserFeed(String authorizationHeader, int page) {
        Long currentUserId = getUserId(authorizationHeader);
        PageDto<FeedItemDto> feedPageCached = cacheService.getCachedFeed(currentUserId, page);
        if (feedPageCached != null) {
            return feedPageCached;
        }
        PageDto<FeedItemDto> feedPage = getUserFeedOrCache(currentUserId, page);
        cacheService.cacheFeed(currentUserId, page, feedPage);
        return feedPage;
    }

    public PageDto<FeedItemDto> getTrending() {
        if (cacheService.getCachedTrending() != null) {
            return cacheService.getCachedTrending();
        }
        LocalDateTime time = LocalDateTime.now().minusHours(24);
        Pageable pageable = PageRequest.of(0, 100);
        Page<FeedItem> feedItems = repository.findByCreatedAtAfterOrderByTrendScoreDesc(time, pageable);
        List<FeedItemDto> feedItemDtoList = feedItems.stream()
                .map(mapper::toDto).toList();
        return new PageDto<>(0, 100, feedItems.getTotalElements(), feedItems.getTotalPages(), feedItemDtoList);
    }

    private PageDto<FeedItemDto> getUserFeedOrCache(Long currentUserId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FeedItem> feedPage = repository.findAllByUserIdAndIsVisibleTrueOrderByFeedCreatedAtDesc(currentUserId, pageable); //proverit order by
        List<FeedItemDto> content = feedPage.stream()
                .map(mapper::toDto).toList();
        return new PageDto<>(page, 20, feedPage.getTotalElements(), feedPage.getTotalPages(), content);
    }

    public void handleCreatePostEvent(CreatePostEvent event) {
        UserDto userDto;
        if (event.getIsRepost()) {
            userDto = userFeignClient.getProfile(event.getRepostedByUserId());
        } else {
            userDto = userFeignClient.getProfile(event.getAuthorId());
        }
        String repostOwnerUsername = null;
        if (event.getIsRepost()) {
            UserDto repostOwner = userFeignClient.getProfile(event.getRepostedByUserId());
            repostOwnerUsername = repostOwner.getUsername();
        }
        final String finalRepostOwnerUsername = repostOwnerUsername;
        List<FeedItem> feedItems = userDto.getFollowers().stream()
                .map(followerId -> {
                    FeedItem feedItem = new FeedItem();
                    feedItem.setUserId(followerId);
                    feedItem.setPostId(event.getPostId());
                    feedItem.setAuthorId(event.getAuthorId());
                    feedItem.setAuthorUsername(userDto.getUsername());
                    feedItem.setContent(event.getContent());
                    feedItem.setTopic(event.getTopic());
                    feedItem.setCreatedAt(event.getCreatedAt());
                    feedItem.setFeedCreatedAt(LocalDateTime.now());
                    feedItem.setIsRepost(event.getIsRepost());
                    feedItem.setOriginalPostId(event.getOriginalPostId());
                    feedItem.setRepostedByUserId(event.getRepostedByUserId());
                    feedItem.setRepostedByUsername(finalRepostOwnerUsername);
                    feedItem.setMediaUrls(event.getMediaUrls());
                    feedItem.setIsVisible(true);
                    feedItem.setLikeCount(event.getLikeCount());
                    feedItem.setRepostCount(event.getRepostCount());
                    feedItem.setReplyCount(event.getReplyCount());
                    feedItem.setSendCount(event.getSendCount());
                    return feedItem;
                }).toList();
        repository.saveAll(feedItems);

        feedItems.stream().map(FeedItem::getUserId)
                .distinct()
                .forEach(userId -> {
                    for (int i = 0; i < 3; i++) {
                        cacheService.evictFeed(userId, i);
                    }
                });
    }

    public void handleUpdatePostEvent(UpdatePostEvent updatePostEvent) {
        List<FeedItem> feedItems = repository.findByPostId(updatePostEvent.getPostId());
        feedItems.forEach(feedItem -> {
            feedItem.setContent(updatePostEvent.getContent());
            feedItem.setTopic(updatePostEvent.getTopic());
        });
        repository.saveAll(feedItems);

        feedItems.stream().map(FeedItem::getUserId)
                .distinct()
                .forEach(userId -> {
                    for (int i = 0; i < 3; i++) {
                        cacheService.evictFeed(userId, i);
                    }
                });
    }

    public void handlePostStatusEvent(PostStatusEvent postStatusEvent) {
        List<FeedItem> feedItems = repository.findByPostId(postStatusEvent.getPostId());
        feedItems.forEach(feedItem -> {
            feedItem.setLikeCount(postStatusEvent.getLikeCount());
            feedItem.setRepostCount(postStatusEvent.getRepostCount());
            feedItem.setReplyCount(postStatusEvent.getReplyCount());
            feedItem.setSendCount(postStatusEvent.getSendCount());
        });
        repository.saveAll(feedItems);
    }

    public void handleUsernameUpdateEvent(UsernameUpdateEvent usernameUpdateEvent) {
        List<FeedItem> feedItem = repository.findByAuthorId(usernameUpdateEvent.getUserId());
        feedItem.forEach(feed -> feed.setAuthorUsername(usernameUpdateEvent.getUsername()));
        repository.saveAll(feedItem);
    }

    @Transactional
    public void handlePostDeleteEvent(Long postId) {
        repository.deleteAllByPostId(postId);
    }

    @Transactional
    public void handleRepostDeleteEvent(Long postId) {
        repository.deleteAllByOriginalPostId(postId);
    }

    public void handlePostArchiveEvent(Long postId) {
        List<FeedItem> feedItems = repository.findByPostId(postId);
        List<FeedItem> feedItemReposts = repository.findByOriginalPostId(postId);
        feedItems.forEach(feed -> feed.setIsVisible(false));
        feedItemReposts.forEach(feed -> feed.setIsVisible(false));
        repository.saveAll(feedItems);
        repository.saveAll(feedItemReposts);
    }
    public void handlePostUnarchiveEvent(Long postId) {
        List<FeedItem> feedItems = repository.findByPostId(postId);
        feedItems.forEach(feed -> feed.setIsVisible(true));
        repository.saveAll(feedItems);
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
