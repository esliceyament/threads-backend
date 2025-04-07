package com.threads.userservice.repository;

import com.threads.userservice.entity.Follow;
import com.threads.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowingId(Long follower, Long following);
    Optional<Follow> findByFollowerAndFollowing(UserProfile follower, UserProfile following);
    List<Follow> findByFollowingId(Long id);
    List<Follow> findByFollowerId(Long id);
    void deleteByFollowerIdAndFollowingId(Long follower, Long following);
}
