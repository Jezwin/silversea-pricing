package com.silversea.aem.services;

import com.day.cq.wcm.api.Page;

import java.util.function.Supplier;

public interface GlobalCacheService {

    void clear();

    <T> T getCache(String textKey, Class<T> typeKey, Supplier<T> o);

    boolean containsKey(String key);

    default <T> T getCache(Page page, Class<T> type, Supplier<T> o) {
        return getCache(page.getPath(), type, o);
    }

    default <T> T getCache(String textKey, TypeReference<T> type, Supplier<T> o) {
        return getCache(textKey, type.getType(), o);
    }

    default <T> T getCache(Page page, TypeReference<T> type, Supplier<T> o) {
        return getCache(page.getPath(), type, o);
    }
}
