package org.bobpark.finance.domain.role.feign.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.bobpark.finance.domain.role.feign.model.RoleResponse;

@FeignClient(name = "authorization-service", contextId = "user-role-service")
public interface RoleClient {

    @GetMapping(path = "role")
    List<RoleResponse> getRoles();
}
