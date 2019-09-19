package com.silversea.aem.helper.crx;

public interface CrxQuerier {
    <T> T get(String path, Class<T> objectClass) throws Exception;
}
