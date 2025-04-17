package com.threads.postservice.service.implementation;

import com.threads.postservice.entity.Post;
import com.threads.postservice.entity.ReportedPost;
import com.threads.postservice.enums.ReportStatus;
import com.threads.postservice.exception.NotFoundException;
import com.threads.postservice.feign.SecurityFeignClient;
import com.threads.postservice.payload.ReportRequest;
import com.threads.postservice.repository.PostRepository;
import com.threads.postservice.repository.ReportRepository;
import com.threads.postservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Long currentUserId = getUserId(authorizationHeader);
        Post post = postRepository.findByIdAndHiddenFalse(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found!"));
        accessGuard.checkUserAccessToPost(currentUserId, post.getAuthorId());
        if (currentUserId.equals(post.getAuthorId())) {
            throw new IllegalArgumentException("Cannot report own post!");
        }
        Optional<ReportedPost> reportOpt = reportRepository.findByReporterIdAndTargetId(currentUserId, postId);
        if (reportOpt.isPresent()) {
            if (!reportOpt.get().getReason().equals(request.getReason())) {
                ReportedPost reportedPost = reportOpt.get();
                reportedPost.setReason(request.getReason());
                reportedPost.setReportedAt(LocalDateTime.now());
                reportRepository.save(reportedPost);
            }
            return;
        }
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
    @Transactional
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
            reportRepository.deleteByTargetId(post.getId());
            return;
        }
        if (status.equals(ReportStatus.REJECTED)) {
            Post post = postRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new NotFoundException("Post " + report.getTargetId() + " not found!"));
            post.setReportCount(post.getReportCount() - 1);
            postRepository.save(post);
            reportRepository.deleteByReporterIdAndTargetId(report.getReporterId(), post.getId());
            return;
        }
        report.setStatus(status);
        reportRepository.save(report);
    }

    @Override
    public List<ReportedPost> getReports(ReportStatus status) {
        return reportRepository.findAllByStatus(status);
    }

    @Override
    public List<ReportedPost> getReportsOfPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("No post " + postId);
        }
        return reportRepository.findByTargetId(postId);
    }

    private Long getUserId(String authorizationHeader) {
        return securityFeignClient.getUserId(authorizationHeader);
    }
}
