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
    public <T> T get(String path, Class<T> objectClass) {
        return (T) this.repo.get(path);
    }

    @Override
    public HashMap<String, Object> get(String path) throws Exception {
        return (HashMap<String, Object>) this.repo.get(path);
    }
}
