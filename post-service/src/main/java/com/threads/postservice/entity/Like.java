package com.threads.postservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long postId;

    private LocalDateTime likedAt;

    public Like(Long userId, Long postId, LocalDateTime likedAt) {
        this.userId = userId;
        this.postId = postId;
        this.likedAt = likedAt;
    }

}
