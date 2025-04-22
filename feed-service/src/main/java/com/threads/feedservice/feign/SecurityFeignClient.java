package com.threads.feedservice.feign;

import com.threads.feedservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface SecurityFeignClient {

    @GetMapping("/authentication/get-id")
    Long getUserId(@RequestHeader("Authorization") String token);

    @PostMapping("/access-guard/user-feed/{id}")
    UserDto getProfile(@PathVariable Long id);

}
