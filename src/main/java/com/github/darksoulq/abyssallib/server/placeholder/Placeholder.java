package com.github.darksoulq.abyssallib.server.placeholder;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public abstract class Placeholder<T> {

    private final Key id;
    private final Class<T> type;
    private final Codec<T> codec;
    private final DynamicOps<String> ops;

    public Placeholder(Key id, Class<T> type) {
        this(id, type, null, null);
    }

    public Placeholder(Key id, Class<T> type, Codec<T> codec, DynamicOps<String> ops) {
        this.id = id;
        this.type = type;
        this.codec = codec;
        this.ops = ops;
    }

    public Key getId() {
        return id;
    }

    public Class<T> getType() {
        return type;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public DynamicOps<String> getOps() {
        return ops;
    }

    public abstract PlaceholderResult<T> resolve(PlaceholderContext context);

    public Component format(T value) {
        if (value instanceof Component c) return c;
        if (value instanceof Integer i) return Component.text(i);
        if (value instanceof Double d) return Component.text(d);
        if (value instanceof Float f) return Component.text(f);
        if (value instanceof Boolean b) return Component.text(b);
        
        if (codec != null && ops != null) {
            try {
                return Component.text(codec.encode(ops, value));
            } catch (Exception ignored) {}
        }
        return Component.text(String.valueOf(value));
    }
}