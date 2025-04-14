package com.threads.postservice.entity;

import com.threads.postservice.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PostMedia {
    @Id
    @GeneratedValue
    private Long id;

    private Long postId;

    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private MediaType type;
}
