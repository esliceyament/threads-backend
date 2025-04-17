package com.threads.postservice.repository;

import com.threads.postservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOriginalPostId(Long id);
    List<Post> findByOriginalPostIdOrderByCreatedAtAsc(Long id);
    List<Post> findByOriginalPostIdAndHiddenFalse(Long id);
    Optional<Post> findByIdAndHiddenFalse(Long id);
    List<Post> findByAuthorIdAndIsPostTrueAndHiddenFalse(Long authorId);
    List<Post> findByAuthorIdAndIsReplyTrueAndHiddenFalse(Long authorId);
    List<Post> findByAuthorIdAndIsPostTrueAndHiddenTrue(Long authorId);
}
