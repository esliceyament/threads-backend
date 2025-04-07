package com.threads.authservice.controller;

import com.threads.authservice.dto.UserDto;
import com.threads.authservice.dto.UserLoginDto;
import com.threads.authservice.service.JwtUtil;
import com.threads.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class UserController {
    private final UserService service;
    private final JwtUtil jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDto dto) {
        return service.login(dto);
    }

    @GetMapping("/get-id")
    public Long getUserId(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        return jwtService.extractId(jwt);
    }
}
