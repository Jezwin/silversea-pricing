package com.silversea.aem.helper.content;

import java.util.HashMap;

public interface ContentLoader {
    <T> T get(String path, Class<T> objectClass) throws Exception;

    HashMap<String, Object> get(String path) throws Exception;
}
