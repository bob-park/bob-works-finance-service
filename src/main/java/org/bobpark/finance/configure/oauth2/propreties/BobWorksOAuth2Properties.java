package org.bobpark.finance.configure.oauth2.propreties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bob-works.oauth2")
public record BobWorksOAuth2Properties(List<String> scopes,
                                       String clientId,
                                       String clientSecret) {
}
