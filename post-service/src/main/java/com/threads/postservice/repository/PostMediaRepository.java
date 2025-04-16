package com.threads.postservice.repository;

import com.threads.postservice.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    void deleteByPostId(Long postId);
}
