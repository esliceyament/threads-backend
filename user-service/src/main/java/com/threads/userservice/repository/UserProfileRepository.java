package com.threads.userservice.repository;

import com.threads.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByUsername(String username);
    Optional<UserProfile> findById(Long id);
    @Query("""
        SELECT u FROM UserProfile u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT(:query, '%')) 
""")
    List<UserProfile> searchProfiles(@Param("query") String query);

}
