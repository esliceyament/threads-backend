package com.threads.userservice.repository;

import com.threads.userservice.entity.Block;
import com.threads.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerIdAndBlockedId(Long blocker, Long blocked);
    Optional<Block> findByBlockerAndBlocked(UserProfile blocker, UserProfile blocked);
    List<Block> findByBlockerId(Long id);
}
