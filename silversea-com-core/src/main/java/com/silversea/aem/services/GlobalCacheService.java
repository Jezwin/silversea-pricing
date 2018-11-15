package com.silversea.aem.services;

import java.util.function.Supplier;

public interface GlobalCacheService {

    void clear();

    <T> T getCache(String textKey, Class<T> typeKey, Supplier<T> o);

    boolean containsKey(String key);

    default <T> T getCache(String textKey, TypeReference<T> type, Supplier<T> o){
        return getCache(textKey, type.getType(), o);
    }
}
