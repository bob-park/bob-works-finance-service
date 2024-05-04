package org.bobpark.finance.common.utils;

import static org.apache.commons.lang3.math.NumberUtils.*;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bobpark.finance.domain.user.feign.model.UserResponse;

public interface AuthUtils {
    ObjectMapper mapper = new ObjectMapper();

    static UserResponse parseToUserResponse(Principal principal) {

        Jwt jwt = ((JwtAuthenticationToken)principal).getToken();

        Map<String, Object> profile = jwt.getClaim("profile");

        return UserResponse.builder()
            .id(toLong(String.valueOf(profile.get("id"))))
            .email(jwt.getClaimAsString("email"))
            .name((String)profile.get("name"))
            .userId(jwt.getClaimAsString("sub"))
            .avatar((String)profile.get("avatar"))
            .build();
    }
}
