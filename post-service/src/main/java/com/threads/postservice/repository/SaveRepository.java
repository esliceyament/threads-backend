package com.threads.postservice.repository;

import com.threads.postservice.entity.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaveRepository extends JpaRepository<SavedPost, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    void deleteByPostId(Long postId);
    List<SavedPost> findByUserId(Long userId);
}
