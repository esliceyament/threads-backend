package com.threads.postservice.repository;

import com.threads.postservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOriginalPostId(Long id);
    List<Post> findByOriginalPostIdOrderByLikeCountDesc(Long id);
    List<Post> findByOriginalPostIdAndHiddenFalseOrderByLikeCountDesc(Long id);
    Optional<Post> findByIdAndHiddenFalse(Long id);
    List<Post> findByAuthorIdAndIsPostTrueAndHiddenFalseOrderByCreatedAtDesc(Long authorId);
    List<Post> findByAuthorIdAndIsReplyTrueAndHiddenFalseOrderByCreatedAtDesc(Long authorId);
    List<Post> findByAuthorIdAndIsPostTrueAndHiddenTrue(Long authorId);
    Page<Post> findByTopicAndHiddenFalse(String content, Pageable pageable);
}
