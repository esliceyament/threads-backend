package com.threads.postservice.entity;

import com.threads.postservice.enums.ReportReason;
import com.threads.postservice.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ReportedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reporterId;
    private Long targetId;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private String additionalInfo;

    private LocalDateTime reportedAt;

}
