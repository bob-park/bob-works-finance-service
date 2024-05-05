package org.bobpark.finance.common.auth;

public interface BobWorksAuthenticationContextHolder {

    ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    static String getContext() {
        return CONTEXT.get();
    }

    static void setContext(String accessToken) {
        CONTEXT.set(accessToken);
    }

}
