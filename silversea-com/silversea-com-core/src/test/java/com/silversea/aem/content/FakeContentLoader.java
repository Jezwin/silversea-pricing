package com.silversea.aem.content;

import com.silversea.aem.content.ContentLoader;

import java.util.HashMap;

public class FakeContentLoader implements ContentLoader {

    private final HashMap<String, Object> repo;

    public FakeContentLoader() {
        this.repo = new HashMap<>();
    }

    public void addNode(String path, Object node) {
        this.repo.put(path, node);
    }

    @Override
    public <T> T get(String path, Class<T> objectClass) throws Exception {

        T lookup = (T) this.repo.get(path);

        if(lookup == null) {
            String message = String.format("Couldn't find %s at CRX path %s", objectClass.getSimpleName(), path);
            throw new Exception(message);
        }

        if(!lookup.getClass().isAssignableFrom(objectClass)) {
            String message = String.format("Found CRX resource at %s but was unable to convert it to a %s", path, objectClass.getSimpleName());
            throw new Exception(message);
        }

        return lookup;
    }

    @Override
    public HashMap<String, Object> get(String path) throws Exception {
        return (HashMap<String, Object>) this.repo.get(path);
    }
}
