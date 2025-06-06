package com.threads.feedservice.scheduler;

import com.threads.feedservice.dto.FeedItemDto;
import com.threads.feedservice.dto.PageDto;
import com.threads.feedservice.entity.FeedItem;
import com.threads.feedservice.mapper.FeedMapper;
import com.threads.feedservice.repository.FeedRepository;
import com.threads.feedservice.cache.FeedCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrendingPostsScheduler {
    private final FeedRepository feedRepository;
    private final FeedCacheService cacheService;
    private final FeedMapper mapper;

    @Scheduled(fixedRate = 2 * 15 * 1000) //30 * 60 * 1000
    public void updateTrendingPosts() {
        LocalDateTime time = LocalDateTime.now().minusHours(24);
        List<FeedItem> posts = feedRepository.getFeedItemsByCreatedAtAfter(time);
        Map<Long, FeedItem> itemMap = new LinkedHashMap<>();
        for (FeedItem feed : posts) {
            double score = calculateScore(feed);
            feed.setTrendScore(score);
        }
        feedRepository.saveAll(posts);
        Pageable pageable = PageRequest.of(0, 100);
        Page<FeedItem> trendingFeed = feedRepository.findWithMediaUrlsByCreatedAtAfterOrderByTrendScoreDesc(time, pageable);
        trendingFeed.forEach(item -> itemMap.putIfAbsent(item.getPostId(), item));
        List<FeedItemDto> feedItemDtoList = new ArrayList<>(itemMap.values()).stream().map(mapper::toDto).toList();
        cacheService.cacheTrending(new PageDto<>(0, 100, trendingFeed.getTotalElements(), trendingFeed.getTotalPages(), feedItemDtoList));
    }

    @Scheduled(fixedRate = 2 * 60 * 1000) //10 * 24 * 3600 * 1000
    public void removeOldFeeds() {
        LocalDateTime ten_days = LocalDateTime.now().minusDays(10);
        feedRepository.deleteAllByFeedCreatedAtBefore(ten_days);
    }

    private int calculateScore(FeedItem feed) {
        return feed.getLikeCount() + feed.getSendCount() * 2 + feed.getReplyCount() * 2 + feed.getRepostCount() * 3;
    }
}
