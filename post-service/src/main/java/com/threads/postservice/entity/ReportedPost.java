package com.threads.postservice.entity;

import com.threads.postservice.enums.ReportReason;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ReportedPost {
    @Id
    @GeneratedValue
    private Long id;

    private Long reporterId;
    private Long reportedPostId;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    private String additionalInfo;

    private LocalDateTime reportedAt;

}
