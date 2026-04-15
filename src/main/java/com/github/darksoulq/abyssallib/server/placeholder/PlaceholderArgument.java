package com.github.darksoulq.abyssallib.server.placeholder;

import com.github.darksoulq.abyssallib.common.reflection.Result;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderArgument {

    private final PlaceholderContext context;
    private final String raw;

    public PlaceholderArgument(PlaceholderContext context, String raw) {
        this.context = context;
        this.raw = raw;
    }

    public String asString() {
        Placeholder<?> p = Registries.PLACEHOLDERS.get(raw);
        if (p != null) {
            PlaceholderResult<?> res = p.resolve(context);
            if (!res.isEmpty() && !res.isError()) {
                return String.valueOf(res.getValue());
            }
        }
        return raw;
    }

    public <V> Result<V> as(Codec<V> codec, DynamicOps<String> ops) {
        try {
            return Result.success(codec.decode(ops, asString()));
        } catch (Codec.CodecException e) {
            return Result.failure(e);
        }
    }

    public Result<Integer> asInt() {
        try {
            return Result.success(Integer.parseInt(raw));
        } catch (NumberFormatException e1) {
            Placeholder<?> p = Registries.PLACEHOLDERS.get(raw);
            if (p != null) {
                PlaceholderResult<?> res = p.resolve(context);
                if (!res.isEmpty() && !res.isError()) {
                    if (res.getValue() instanceof Number n) return Result.success(n.intValue());
                    try { return Result.success(Integer.parseInt(String.valueOf(res.getValue()))); } 
                    catch (NumberFormatException ignored) {}
                }
            }
            return Result.failure(e1);
        }
    }

    public Result<Double> asDouble() {
        try {
            return Result.success(Double.parseDouble(raw));
        } catch (NumberFormatException e1) {
            Placeholder<?> p = Registries.PLACEHOLDERS.get(raw);
            if (p != null) {
                PlaceholderResult<?> res = p.resolve(context);
                if (!res.isEmpty() && !res.isError()) {
                    if (res.getValue() instanceof Number n) return Result.success(n.doubleValue());
                    try { return Result.success(Double.parseDouble(String.valueOf(res.getValue()))); } 
                    catch (NumberFormatException ignored) {}
                }
            }
            return Result.failure(e1);
        }
    }

    public Result<Boolean> asBoolean() {
        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
            return Result.success(Boolean.parseBoolean(raw));
        }
        
        Placeholder<?> p = Registries.PLACEHOLDERS.get(raw);
        if (p != null) {
            PlaceholderResult<?> res = p.resolve(context);
            if (!res.isEmpty() && !res.isError()) {
                if (res.getValue() instanceof Boolean b) return Result.success(b);
                return Result.success(Boolean.parseBoolean(String.valueOf(res.getValue())));
            }
        }
        return Result.failure(new IllegalArgumentException("Not a boolean"));
    }

    public Result<Player> asPlayer() {
        String target = asString();
        Player player = Bukkit.getPlayerExact(target);
        if (player == null) return Result.failure(new NullPointerException());
        return Result.success(player);
    }
}