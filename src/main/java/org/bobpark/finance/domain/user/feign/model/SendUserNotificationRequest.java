package org.bobpark.finance.domain.user.feign.model;

import lombok.Builder;

@Builder
public record SendUserNotificationRequest(String message) {
}
