package com.threads.userservice.controller;

import com.threads.userservice.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block")
public class BlockController {
    private final BlockService blockService;

    @PostMapping("/block/{blockedId}")
    public ResponseEntity<?> blockUser(@PathVariable Long blockedId, @RequestHeader("Authorization") String authorizationHeader) {
        blockService.blockUser(blockedId, authorizationHeader);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/unblock/{blockedId}")
    public ResponseEntity<?> unblockUser(@PathVariable Long blockedId, @RequestHeader("Authorization") String authorizationHeader) {
        blockService.unblock(blockedId, authorizationHeader);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get-blocked")
    public ResponseEntity<?> getBlockedUsers(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(blockService.getBlockedUsers(authorizationHeader));
    }

}
