package org.bobpark.finance.domain.auth.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.bobpark.finance.domain.auth.feign.model.AuthTokenRequest;
import org.bobpark.finance.domain.auth.feign.model.AuthTokenResponse;

@FeignClient(name = "authorization-service")
public interface BobWorksOAuth2FeignClient {

    @PostMapping(path = "oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AuthTokenResponse token(@RequestBody AuthTokenRequest authRequest);
}
