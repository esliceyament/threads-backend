package com.threads.postservice.repository;

import com.threads.postservice.entity.Repost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepostRepository extends JpaRepository<Repost, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    boolean existsByPostId(Long postId);
    List<Repost> findByUserIdAndHiddenFalse(Long userId);
    void deleteByPostId(Long postId);
    List<Repost> findByPostId(Long postId);
    Optional<Repost> findByIdAndHiddenFalse(Long id);
}
