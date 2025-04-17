package com.threads.postservice.repository;

import com.threads.postservice.entity.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    List<PostShare> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    void deleteByPostId(Long postId);
}
