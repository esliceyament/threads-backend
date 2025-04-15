package com.threads.postservice.repository;

import com.threads.postservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long currentUserId, Long postId);
    List<Like> findByPostId(Long postId);
}
