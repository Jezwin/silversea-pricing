package com.silversea.aem.helper.crx;

public interface ContentLoader {
    <T> T get(String path, Class<T> objectClass) throws Exception;
}
