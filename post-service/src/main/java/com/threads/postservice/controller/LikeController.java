package com.threads.postservice.controller;

import com.threads.postservice.response.LikeResponse;
import com.threads.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/like/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        likeService.likePost(postId, authorizationHeader);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/unlike/{postId}")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        likeService.unlikePost(postId, authorizationHeader);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get-likes/{postId}")
    public ResponseEntity<List<LikeResponse>> getLikesOfPost(@PathVariable Long postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(likeService.getLikesOfPost(postId, authorizationHeader));
    }
}
