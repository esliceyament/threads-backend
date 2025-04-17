package com.threads.postservice.service;

import com.threads.postservice.entity.ReportedPost;
import com.threads.postservice.enums.ReportStatus;
import com.threads.postservice.payload.ReportRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReportService {

    void reportPost(Long postId, ReportRequest request, String authorizationHeader);
    void updateStatus(Long reportId, ReportStatus status);
    List<ReportedPost> getReports(ReportStatus status);
    List<ReportedPost> getReportsOfPost(Long postId);
}
