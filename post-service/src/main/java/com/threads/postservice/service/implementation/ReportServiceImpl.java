package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.ReportedPost;
import com.threads.postservice.enums.ReportStatus;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.payload.ReportRequest;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.ReportRepository;
import com.threads.postservice.service.PostService;
import com.threads.postservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final PostAccessGuard accessGuard;
    private final SecurityFeignClient securityFeignClient;
    private final PostServiceImpl postServiceImpl;

    @Override
    public void reportPost(Long postId, ReportRequest request, String authorizationHeader) {
        Post post = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        Long currentUserId = getUserId(authorizationHeader);
        if (currentUserId.equals(post.getAuthorId())) {
            throw new IllegalArgumentException("Cannot report this post!");
        }
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        post.setReportCount(post.getReportCount() + 1);
        if (post.getReportCount() >= 20) {
            post.setHidden(true);
        }
        ReportedPost report = new ReportedPost();
        report.setReporterId(currentUserId);
        report.setTargetId(postId);
        report.setReason(request.getReason());
        report.setStatus(ReportStatus.PENDING);
        report.setAdditionalInfo(request.getAdditionalInfo());
        report.setReportedAt(LocalDateTime.now());
        postRepository.save(post);
        reportRepository.save(report);
    }

    @Override
    public void updateStatus(Long reportId, ReportStatus status) {
        ReportedPost report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report " + reportId + " not found!"));
        if (status.equals(ReportStatus.RESOLVED)) {
            Post post = postRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new NotFoundException("Post " + report.getTargetId() + " not found!"));
            if (post.getIsPost()) {
                postServiceImpl.deleteOriginalPost(post, post.getAuthorId());
            }
            else {
                postServiceImpl.deleteReply(post, post.getAuthorId());
            }
        }
        report.setStatus(status);
        reportRepository.save(report);
    }

    @Override
    public List<ReportedPost> getReports(ReportStatus status) {
        return reportRepository.findAllByStatus(status);
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
