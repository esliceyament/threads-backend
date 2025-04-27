package com.threads.feedservice.repository;

import com.threads.feedservice.entity.FeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepository extends JpaRepository<FeedItem, Long> {
    Page<FeedItem> findAllByUserIdAndIsVisibleTrue(Long userId, Pageable pageable);
    List<FeedItem> findByPostId(Long postId);
    List<FeedItem> findByAuthorId(Long authorId);
    List<FeedItem> findByOriginalPostId(Long postId);
    void deleteAllByPostId(Long postId);
    void deleteAllByOriginalPostId(Long postId);
    List<FeedItem> getFeedItemsByCreatedAtAfter(LocalDateTime time);
    Page<FeedItem> findByCreatedAtAfterOrderByTrendScoreDesc(LocalDateTime after, Pageable pageable);
}
