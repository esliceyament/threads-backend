package com.threads.userservice.controller;

import com.threads.request.AccessRequest;
import com.threads.userservice.dto.UserFeedDto;
import com.threads.userservice.service.AccessGuard;
import com.threads.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/access-guard")
public class AccessGuardController {
    private final AccessGuard accessGuard;
    private final UserProfileService profileService;

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

    @PostMapping("/user-exists/{userId}")
    public boolean checkUserExists(@PathVariable Long userId) {
        return accessGuard.checkUserExists(userId);
    }

    @PostMapping("/check-send")
    public ResponseEntity<Void> canSendPost(@RequestBody AccessRequest accessRequest) {
        accessGuard.canSendPost(accessRequest.getCurrentUserId(), accessRequest.getOwnerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user-feed/{id}")
    public UserFeedDto getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

}
