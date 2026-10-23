package com.github.darksoulq.abyssallib.server.resource.asset;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RawAsset implements Asset {
    private final String namespace;
    private final String path;
    private final byte[] data;

    public RawAsset(String namespace, String path, byte[] data) {
        this.namespace = namespace;
        this.path = path;
        this.data = data;
    }

    @Override
    public void emit(@NotNull Map<String, byte[]> files) {
        files.put("assets/" + namespace + "/" + path, data);
    }
}