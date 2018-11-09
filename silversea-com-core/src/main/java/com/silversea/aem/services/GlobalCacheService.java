package com.silversea.aem.services;

import org.apache.sling.api.resource.Resource;

import java.util.function.Function;
import java.util.function.Supplier;

public interface GlobalCacheService {

    void clearCache();

    <T> T  getCache(String textKey, Class<T> typeKey, Supplier<T> o);
}
