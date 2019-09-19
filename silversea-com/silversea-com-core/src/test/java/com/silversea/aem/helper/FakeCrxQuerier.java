package com.silversea.aem.helper;

import com.silversea.aem.helper.crx.CrxQuerier;

import java.util.HashMap;

public class FakeCrxQuerier implements CrxQuerier {

    private final HashMap<String, Object> repo;

    public FakeCrxQuerier() {
        this.repo = new HashMap<>();
    }

    public void addNode(String path, Object node) {
        this.repo.put(path, node);
    }

    @Override
    public <T> T get(String path, Class<T> objectClass) {
        return (T) this.repo.get(path);
    }
}
