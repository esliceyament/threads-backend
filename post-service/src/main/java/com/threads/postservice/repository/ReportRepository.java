package com.threads.postservice.repository;

import com.threads.postservice.entity.ReportedPost;
import com.threads.postservice.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportedPost, Long> {
    List<ReportedPost> findAllByStatus(ReportStatus reportStatus);
    boolean existsByReporterIdAndTargetId(Long reporterId, Long targetId);
    Optional<ReportedPost> findByReporterIdAndTargetId(Long reporterId, Long targetId);
    List<ReportedPost> findByTargetId(Long postId);
    void deleteByTargetId(Long postId);
    void deleteByReporterIdAndTargetId(Long userId, Long postId);
}
