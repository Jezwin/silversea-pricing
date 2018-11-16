package com.silversea.aem.services.impl;


import com.silversea.aem.services.GlobalCacheService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Component
public class GlobalCacheServiceImpl implements GlobalCacheService {

    static final private Logger LOGGER = LoggerFactory.getLogger(GlobalCacheServiceImpl.class);
    private Map<String, Object> globalCache = new HashMap<>();

    @Override
    public void clearCache() {
        globalCache.clear();
    }

    @Override
    public <T> T getCache(String textKey, Class<T> typeKey, Supplier<T> o) {
        Object untypedCachedValue = globalCache.computeIfAbsent(textKey, notUsed -> o.get());
        return typeKey.cast(untypedCachedValue);
    }

}
