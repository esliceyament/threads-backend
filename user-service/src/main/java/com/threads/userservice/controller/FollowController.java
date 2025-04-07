package com.threads.userservice.controller;

import com.threads.userservice.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/{targetId}")
    public ResponseEntity<?> follow(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long targetId) {
        followService.followUser(authorizationHeader, targetId);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/unfollow/{targetId}")
    public ResponseEntity<?> unfollow(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long targetId) {
        followService.unfollowUser(authorizationHeader, targetId);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFollow(@PathVariable Long id) {
        return ResponseEntity.ok(followService.getFollow(id));
    }

    @GetMapping("/get-followers/{id}")
    public ResponseEntity<?> getFollowers(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(followService.getFollowers(id, authorizationHeader));
    }

    @GetMapping("/get-followings/{id}")
    public ResponseEntity<?> getFollowings(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(followService.getFollowings(id, authorizationHeader));
    }

    @GetMapping("/get-my-followers")
    public ResponseEntity<?> getMyFollowers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(followService.getMyFollowers(authorizationHeader));
    }

    @GetMapping("/get-my-followings")
    public ResponseEntity<?> getMyFollowings(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(followService.getMyFollowings(authorizationHeader));
    }
}
