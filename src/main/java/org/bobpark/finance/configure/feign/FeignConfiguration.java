package org.bobpark.finance.configure.feign;

import static com.google.common.net.HttpHeaders.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Maps;

import feign.RequestInterceptor;

@Configuration
public class FeignConfiguration {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RequestInterceptor customRequestInterceptor() {
        return requestTemplate -> requestTemplate.headers(getRequestHeaders());
    }

    private Map<String, Collection<String>> getRequestHeaders() {

        Map<String, Collection<String>> headers = Maps.newHashMap();

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            headers.put(X_FORWARDED_FOR, Collections.singletonList(request.getHeader(X_FORWARDED_FOR)));
            headers.put(AUTHORIZATION, Collections.singletonList(request.getHeader(AUTHORIZATION)));
        }

        headers.put(CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        headers.put(ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));

        return headers;
    }
}
