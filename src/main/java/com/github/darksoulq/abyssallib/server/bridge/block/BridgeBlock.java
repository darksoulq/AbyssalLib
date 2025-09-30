package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.util.Identifier;

public record BridgeBlock<T>(Identifier id, String provider, T value) {
    @Override
    public Identifier id() {
        if (provider.equals("minecraft")) return id;
        return Identifier.of(provider, id.getNamespace(), id.getPath());
    }
}
