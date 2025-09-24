package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.util.Identifier;

public class BridgeBlock<T> {
    private final Identifier id;
    public final String provider;
    public final T value;

    public BridgeBlock(Identifier id, String provider, T value) {
        this.id = id;
        this.provider = provider;
        this.value = value;
    }
    public Identifier getId() {
        if (provider.equals("minecraft")) return id;
        return Identifier.of(provider, id.namespace(), id.path());
    }
}
