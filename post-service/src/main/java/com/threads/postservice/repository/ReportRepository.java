package com.threads.postservice.repository;

import com.threads.postservice.entity.ReportedPost;
import com.threads.postservice.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportedPost, Long> {
    List<ReportedPost> findAllByStatus(ReportStatus reportStatus);
}
