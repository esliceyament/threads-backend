package com.threads.userservice.controller;

import com.threads.userservice.dto.UserProfileDto;
import com.threads.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final UserProfileService profileService;

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(profileService.updateProfile(authorizationHeader, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(profileService.getProfile(id, authorizationHeader));
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<?> searchProfile(@PathVariable String username) {
        return ResponseEntity.ok(profileService.searchProfile(username));
    }

    @GetMapping
    public ResponseEntity<?> getMyProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(profileService.getMyProfile(authorizationHeader));
    }
}
