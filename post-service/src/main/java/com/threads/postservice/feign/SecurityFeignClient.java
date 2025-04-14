package com.threads.postservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface SecurityFeignClient {

    @GetMapping("/authentication/get-id")
    Long getUserId(@RequestHeader("Authorization") String token);
}
