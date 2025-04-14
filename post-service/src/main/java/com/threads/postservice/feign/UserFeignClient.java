package com.threads.postservice.feign;

import com.threads.request.AccessRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @PostMapping("/access-guard/profile")
    ResponseEntity<Void> checkAccessToProfile(@RequestBody AccessRequest accessRequest);

    @PostMapping("/access-guard/follower")
    ResponseEntity<Void> checkFollower(@RequestBody AccessRequest accessRequest);
}
