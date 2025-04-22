package com.threads.feedservice.repository;

import com.threads.feedservice.entity.FeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<FeedItem, Long> {
    Page<FeedItem> findAllByUserIdAndIsVisibleTrue(Long userId, Pageable pageable);
    List<FeedItem> findByPostId(Long postId);
    List<FeedItem> findByAuthorId(Long authorId);
    void deleteAllByPostId(Long postId);
}
