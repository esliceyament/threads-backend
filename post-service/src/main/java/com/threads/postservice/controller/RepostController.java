package com.threads.postservice.controller;

import com.threads.postservice.response.RepostResponse;
import com.threads.postservice.service.RepostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repost")
public class RepostController {
    private final RepostService repostService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> repostPost(@PathVariable Long postId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        repostService.repostPost(postId, authorizationHeader);
        return ResponseEntity.ok("Post " + postId + " was successfully reposted!");
    }

    @DeleteMapping("/{repostId}")
    public ResponseEntity<?> removeRepost(@PathVariable Long repostId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        repostService.removeRepost(repostId, authorizationHeader);
        return ResponseEntity.ok("Repost " + repostId + " was successfully removed!");
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<List<RepostResponse>> getUserReposts(@PathVariable Long authorId,
                                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(repostService.getUserReposts(authorId, authorizationHeader));
    }

    @GetMapping
    public ResponseEntity<List<RepostResponse>> getMyReposts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(repostService.getMyReposts(authorizationHeader));
    }
}
