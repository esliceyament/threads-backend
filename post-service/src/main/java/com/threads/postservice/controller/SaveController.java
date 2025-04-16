package com.threads.postservice.controller;

import com.threads.postservice.response.PostResponse;
import com.threads.postservice.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/save")
public class SaveController {
    private final SaveService saveService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> savePost(@PathVariable Long postId,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        saveService.savePost(postId, authorizationHeader);
        return ResponseEntity.ok("Post " + postId + " was successfully saved!");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> unsavePost(@PathVariable Long postId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        saveService.unsavePost(postId, authorizationHeader);
        return ResponseEntity.ok("Post " + postId + " was successfully unsaved!");
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getMySaves(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(saveService.getMySaves(authorizationHeader));
    }
}
