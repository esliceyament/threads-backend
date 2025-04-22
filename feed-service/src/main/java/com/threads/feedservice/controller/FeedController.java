package com.threads.feedservice.controller;

import com.threads.feedservice.service.implementation.FeedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedServiceImpl service;

    @GetMapping
    public ResponseEntity<?> getUserFeed(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestParam(value = "page", defaultValue = "1") int page) {
        return ResponseEntity.ok(service.getUserFeed(authorizationHeader, page));
    }
}
