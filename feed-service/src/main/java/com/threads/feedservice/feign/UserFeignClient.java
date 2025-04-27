package com.threads.feedservice.feign;

import com.threads.feedservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserFeignClient {
    @PostMapping("/access-guard/user-feed/{id}")
    UserDto getProfile(@PathVariable Long id);
}
