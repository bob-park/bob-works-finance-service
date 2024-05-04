package org.bobpark.finance.domain.user.feign.model;

import lombok.Builder;

@Builder
public record UserResponse(Long id,
                           String userId,
                           String email,
                           String name,
                           String avatar) {
}
