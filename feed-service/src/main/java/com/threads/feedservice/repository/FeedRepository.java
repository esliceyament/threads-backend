package com.threads.feedservice.repository;

import com.threads.feedservice.entity.FeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<FeedItem, Long> {
    Page<FeedItem> findAllByUserId(Long userId, Pageable pageable);
}
