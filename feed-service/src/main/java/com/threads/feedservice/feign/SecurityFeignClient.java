package com.threads.feedservice.feign;

import com.threads.feedservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface SecurityFeignClient {

    @GetMapping("/authentication/get-id")
    Long getUserId(@RequestHeader("Authorization") String token);

    @PostMapping("/access-guard/user-feed")
    UserDto getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

}
