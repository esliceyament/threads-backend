package com.threads.postservice.controller;

import com.threads.postservice.payload.ShareRequest;
import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.PostShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/share")
public class PostShareController {
    private final PostShareService postShareService;

    @PostMapping("/{receiverId}")
    public ResponseEntity<?> sendPost(@RequestBody ShareRequest request, @PathVariable Long receiverId,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postShareService.sendPost(request, receiverId, authorizationHeader);
        return ResponseEntity.ok("Post " + request.getPostId() + " was successfully sent!");
    }

    @GetMapping("/shared-by-user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsSharedByUser(@PathVariable Long userId,
                                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postShareService.getPostsSharedByUser(userId, authorizationHeader));
    }

    @GetMapping("/sent-to-user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsSentToUser(@PathVariable Long userId,
                                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(postShareService.getPostsSentToUser(userId, authorizationHeader));
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<?> deleteShare(@PathVariable Long shareId,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        postShareService.deleteShare(shareId, authorizationHeader);
        return ResponseEntity.ok("Post " + shareId + " was successfully deleted!");
    }
}
