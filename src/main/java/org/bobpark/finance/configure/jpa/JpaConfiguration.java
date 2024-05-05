package org.bobpark.finance.configure.jpa;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.apache.commons.lang.StringUtils;

import org.bobpark.finance.common.auth.BobWorksAuthenticationContextHolder;

@EnableJpaAuditing
@Configuration
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {

            String name = "";

            JwtAuthenticationToken authentication = (JwtAuthenticationToken)SecurityContextHolder.getContext()
                .getAuthentication();

            if (authentication != null) {
                name = authentication.getName();
            }

            if (StringUtils.isBlank(name)) {
                name = "system";
            }

            return Optional.of(name);
        };
    }
}
