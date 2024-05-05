package org.bobpark.finance.domain.user.feign.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.bobpark.finance.domain.user.feign.model.SendUserNotificationRequest;
import org.bobpark.finance.domain.user.feign.model.SendUserNotificationResponse;
import org.bobpark.finance.domain.user.feign.model.UserResponse;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping(path = "user")
    UserResponse getUser(@RequestParam("userId") String userId);

    @GetMapping(path = "user/search")
    List<UserResponse> getUsers(@RequestParam("ids") List<Long> ids);

    @PostMapping(path = "v1/user/{userId:\\d+}/notification/send")
    SendUserNotificationResponse sendNotification(@PathVariable long userId,
        @RequestBody SendUserNotificationRequest sendRequest);

}
