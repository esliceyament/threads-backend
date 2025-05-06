package com.threads.feedservice.repository;

import com.threads.feedservice.entity.FeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepository extends JpaRepository<FeedItem, Long> {
    Page<FeedItem> findAllByUserIdAndIsVisibleTrueOrderByFeedCreatedAtDesc(Long userId, Pageable pageable);
    List<FeedItem> findByPostId(Long postId);
    List<FeedItem> findByAuthorId(Long authorId);
    List<FeedItem> findByOriginalPostId(Long postId);
    void deleteAllByPostId(Long postId);
    void deleteAllByOriginalPostId(Long postId);
    void deleteAllByFeedCreatedAtBefore(LocalDateTime time);
    List<FeedItem> getFeedItemsByCreatedAtAfter(LocalDateTime time);
    @Query("SELECT f FROM FeedItem f LEFT JOIN FETCH f.mediaUrls WHERE f.createdAt > :time ORDER BY f.trendScore DESC")
    Page<FeedItem> findWithMediaUrlsByCreatedAtAfterOrderByTrendScoreDesc(@Param("time") LocalDateTime after, Pageable pageable);
}
