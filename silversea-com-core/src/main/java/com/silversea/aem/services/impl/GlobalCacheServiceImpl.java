package com.silversea.aem.services.impl;


import com.silversea.aem.services.GlobalCacheService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
@Component
public class GlobalCacheServiceImpl implements GlobalCacheService {

    static final private Logger LOGGER = LoggerFactory.getLogger(GlobalCacheServiceImpl.class);
    private Map<String, Map<Class<?>, Object>> globalCache = new HashMap<>();

    public void clearCache() {
        globalCache.clear();
    }

    public <T> void setCache(String textKey, Class<T> typeKey, T cachedValue) {
        Map<Class<?>, Object> mapForTextKey = getMapForTextKey(textKey);
        mapForTextKey.put(typeKey, cachedValue);
    }

    public <T> T getCache(String textKey, Class<T> typeKey) {
        Object untypedCachedValue = getUntyped(textKey, typeKey);
        return typeKey.cast(untypedCachedValue);
    }

    private Map<Class<?>, Object> getMapForTextKey(String textKey) {
        if (!globalCache.containsKey(textKey))
            globalCache.put(textKey, new HashMap<>());
        return globalCache.get(textKey);
    }

    private Object getUntyped(String textKey, Class<?> typeKey) {
        if (globalCache.containsKey(textKey))
            return globalCache.get(textKey).get(typeKey);
        else
            return null;
    }
}
