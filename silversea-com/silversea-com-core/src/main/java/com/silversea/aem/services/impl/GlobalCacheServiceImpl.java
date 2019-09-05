package com.silversea.aem.services.impl;


import com.silversea.aem.importers.services.impl.ImportResult;
import com.silversea.aem.services.GlobalCacheService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Service(value = GlobalCacheService.class)
@Component
public class GlobalCacheServiceImpl implements GlobalCacheService {

    private Map<String, Object> globalCache = new HashMap<>();

    @Override
    public ImportResult clear() {
        ImportResult importResult = new ImportResult();
        globalCache.clear();
        importResult.incrementSuccessNumber();
        return importResult;
    }

    @Override
    public <T> T getCache(String textKey, Class<T> typeKey, Supplier<T> supplier) {
        T value;
        if (!globalCache.containsKey(textKey)) {
            value = supplier.get();
            globalCache.put(textKey, value);//this includes null values
        } else {
            value = typeKey.cast(globalCache.get(textKey));
        }
        return value;
    }

    @Override
    public boolean containsKey(String key) {
        return globalCache.containsKey(key);
    }

}
