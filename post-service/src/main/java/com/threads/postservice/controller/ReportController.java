package com.threads.postservice.controller;

import com.threads.postservice.entity.ReportedPost;
import com.threads.postservice.enums.ReportStatus;
import com.threads.postservice.payload.ReportRequest;
import com.threads.postservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/report/{postId}")
    public ResponseEntity<?> reportPost(@PathVariable Long postId, @RequestBody ReportRequest request,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        reportService.reportPost(postId, request, authorizationHeader);
        return ResponseEntity.ok("Post " + postId + " was successfully reported!");
    }

    @PutMapping("/update-status/{reportId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long reportId, @RequestParam ReportStatus status) {
        reportService.updateStatus(reportId, status);
        return ResponseEntity.ok("Report " + reportId  + " was successfully updated to " + status);
    }

    @GetMapping()
    public ResponseEntity<List<ReportedPost>> getReports(@RequestParam String status) {
        return ResponseEntity.ok(reportService.getReports(ReportStatus.valueOf(status)));
    }

    @GetMapping("/{postId}")
    public List<ReportedPost> getReportsOfPost(@PathVariable Long postId) {
        return reportService.getReportsOfPost(postId);
    }
}
