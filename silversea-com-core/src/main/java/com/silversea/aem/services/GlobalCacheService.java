package com.silversea.aem.services;

public interface GlobalCacheService {

    <T> T getCache(String textKey, Class<T> typeKey);

    <T> void setCache(String textKey, Class<T> typeKey, T chachedValue);

    void clearCache();
}
