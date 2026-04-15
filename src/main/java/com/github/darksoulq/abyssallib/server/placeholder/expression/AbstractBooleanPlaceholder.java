package com.github.darksoulq.abyssallib.server.placeholder.expression;

import com.github.darksoulq.abyssallib.server.placeholder.Placeholder;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderArgument;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderContext;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderResult;
import net.kyori.adventure.key.Key;

public abstract class AbstractBooleanPlaceholder extends Placeholder<Boolean> implements Expressionable<Boolean> {

    public AbstractBooleanPlaceholder(Key id) {
        super(id, Boolean.class);
    }

    @Override public PlaceholderResult<Boolean> add(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> sub(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> mul(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> div(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> mod(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> pow(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> min(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> max(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }

    @Override public PlaceholderResult<Boolean> sin(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> cos(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> tan(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> asin(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> acos(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> atan(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> round(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> floor(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> ceil(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> abs(PlaceholderContext ctx) { return PlaceholderResult.empty(); }

    @Override
    public PlaceholderResult<Boolean> eq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Boolean> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Boolean val = other.asBoolean().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue().equals(val)) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> neq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Boolean> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Boolean val = other.asBoolean().getOrNull();
        return val != null ? PlaceholderResult.success(!res.getValue().equals(val)) : PlaceholderResult.empty();
    }

    @Override public PlaceholderResult<Boolean> gt(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> lt(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> gte(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> lte(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }

    @Override
    public PlaceholderResult<Boolean> and(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Boolean> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Boolean val = other.asBoolean().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() && val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> or(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Boolean> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Boolean val = other.asBoolean().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() || val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> xor(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Boolean> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Boolean val = other.asBoolean().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() ^ val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> not(PlaceholderContext ctx) {
        PlaceholderResult<Boolean> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? PlaceholderResult.empty() : PlaceholderResult.success(!res.getValue());
    }
}