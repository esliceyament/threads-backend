package com.threads.postservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long postId;

    private LocalDateTime likedAt;
}
