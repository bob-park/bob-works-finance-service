package org.bobpark.finance.domain.user.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.bobpark.finance.domain.user.feign.model.UserResponse;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping(path = "user")
    UserResponse getUser(@RequestParam("userId") String userId);

}
