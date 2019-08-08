package com.burgstaller.okhttp;

import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;

/**
 * An authenticator decorator which saves the generated authentication headers for a specific host.
 * To be used in tandem with {@link AuthenticationCacheInterceptor}.
 * Depending on your use case you will probably need to use a {@link java.util.concurrent.ConcurrentHashMap}.
 */
public class CachingAuthenticatorDecorator implements Authenticator {
    private final Authenticator innerAuthenticator;
    private final Map<String, CachingAuthenticator> authCache;

    public CachingAuthenticatorDecorator(Authenticator innerAuthenticator, Map<String, CachingAuthenticator> authCache) {
        this.innerAuthenticator = innerAuthenticator;
        this.authCache = authCache;
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        Request authenticated = innerAuthenticator.authenticate(proxy, response);
        if (authenticated != null) {
            String authorizationValue = authenticated.header("Authorization");
            if (authorizationValue != null && innerAuthenticator instanceof CachingAuthenticator) {
                authCache.put(authenticated.url().getHost(), (CachingAuthenticator) innerAuthenticator);
            }
        }
        return authenticated;
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return innerAuthenticator.authenticateProxy(proxy, response);
    }
}
