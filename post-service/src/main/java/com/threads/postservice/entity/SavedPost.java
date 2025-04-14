package com.threads.postservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SavedPost {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long postId;
}
