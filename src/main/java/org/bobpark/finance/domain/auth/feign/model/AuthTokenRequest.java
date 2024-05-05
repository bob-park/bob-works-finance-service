package org.bobpark.finance.domain.auth.feign.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class AuthTokenRequest {

    @feign.form.FormProperty("grant_type")
    private String grantType;

    private String scope;

    @feign.form.FormProperty("client_id")
    private String clientId;

    @feign.form.FormProperty("client_secret")
    private String clientSecret;

    @Builder
    private AuthTokenRequest(String grantType, String scope, String clientId, String clientSecret) {
        this.grantType = grantType;
        this.scope = scope;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
