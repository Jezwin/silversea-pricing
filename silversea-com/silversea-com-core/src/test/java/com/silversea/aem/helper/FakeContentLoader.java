package com.silversea.aem.helper;

import com.silversea.aem.helper.crx.ContentLoader;

import java.util.HashMap;

public class FakeContentLoader implements ContentLoader {

    private final HashMap<String, Object> repo;

    public FakeContentLoader() {
        this.repo = new HashMap<>();
    }

    public FakeContentLoader(HashMap<String, Object> nodes) {
        this.repo = nodes;
    }

    public void addNode(String path, Object node) {
        this.repo.put(path, node);
    }

    @Override
    public <T> T get(String path, Class<T> objectClass) {
        return (T) this.repo.get(path);
    }
}
