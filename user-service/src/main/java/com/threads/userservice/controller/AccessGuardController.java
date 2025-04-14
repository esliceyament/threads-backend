package com.threads.userservice.controller;

import com.threads.request.AccessRequest;
import com.threads.userservice.service.AccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/access-guard")
public class AccessGuardController {
    private final AccessGuard accessGuard;

    @PostMapping("/profile")
    public ResponseEntity<Void> checkAccessToProfile(@RequestBody AccessRequest accessRequest) {
        accessGuard.checkAccessToProfile(accessRequest.getCurrentUserId(), accessRequest.getOwnerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/follower")
    public ResponseEntity<Void> checkFollower(@RequestBody AccessRequest accessRequest) {
        accessGuard.checkFollower(accessRequest.getCurrentUserId(), accessRequest.getOwnerId());
        return ResponseEntity.ok().build();
    }

}
