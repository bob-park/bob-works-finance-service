package org.bobpark.finance.domain.role.feign.model;

import java.util.List;

public record RoleResponse(Long id,
                           String roleName,
                           String description,
                           RoleResponse parent,
                           List<RoleResponse> children) {
}
