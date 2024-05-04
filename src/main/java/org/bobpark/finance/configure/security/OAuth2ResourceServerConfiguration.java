package org.bobpark.finance.configure.security;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import org.bobpark.finance.configure.security.converter.JwtRoleGrantAuthoritiesConverter;
import org.bobpark.finance.configure.security.handler.RestAuthenticationEntryPoint;
import org.bobpark.finance.domain.role.feign.client.RoleClient;
import org.bobpark.finance.domain.role.feign.model.RoleResponse;

@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class OAuth2ResourceServerConfiguration {

    private final ObjectMapper om;
    private final RoleClient roleClient;

    @Bean
    public SecurityFilterChain resourceSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
            requests ->
                requests.anyRequest().authenticated());

        http.oauth2ResourceServer(
            resourceServer ->
                resourceServer.jwt(
                    jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.exceptionHandling(
            exceptionHandler ->
                exceptionHandler.authenticationEntryPoint(authenticationEntryPoint()));

        return http.build();
    }

    /*
    authentication entry point
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint(om);
    }


    @Bean
    public static MethodSecurityExpressionHandler expressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

        expressionHandler.setRoleHierarchy(roleHierarchy);

        return expressionHandler;
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

        Map<String, List<String>> roleHierarchyMap = parseRoleHierarchyMap();

        String rolesHierarchyStr = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);

        log.debug("role hierarchy={}", rolesHierarchyStr);

        roleHierarchy.setHierarchy(rolesHierarchyStr);

        return roleHierarchy;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtRoleGrantAuthoritiesConverter());

        return jwtAuthenticationConverter;
    }

    private Map<String, List<String>> parseRoleHierarchyMap() {

        Map<String, List<String>> result = Maps.newHashMap();

        List<RoleResponse> roles = roleClient.getRoles();

        for (RoleResponse role : roles) {

            if (role.children().isEmpty()) {
                continue;
            }

            result.put(role.roleName(), role.children().stream().map(RoleResponse::roleName).toList());
        }

        return result;
    }
}
