package org.bobpark.finance.domain.auth.feign.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(@JsonProperty("access_token") String accessToken) {
}
