package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import org.bukkit.Location;

public abstract class BridgeBlock<T> {
    protected final Identifier id;
    protected final String provider;
    protected final T value;

    public BridgeBlock(Identifier id, String provider, T value) {
        this.id = id;
        this.provider = provider;
        this.value = value;
    }

    public abstract void place(Location location) throws Exception;

    public Identifier id() {
        if (provider.equals("minecraft")) return id;
        return Identifier.of(provider, id.getNamespace(), id.getPath());
    }

    public String provider() {
        return provider;
    }

    public T value() {
        return value;
    }

    public Identifier getRawId() {
        return id;
    }
}