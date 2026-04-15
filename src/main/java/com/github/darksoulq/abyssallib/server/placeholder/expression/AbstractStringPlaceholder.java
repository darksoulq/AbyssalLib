package com.github.darksoulq.abyssallib.server.placeholder.expression;

import com.github.darksoulq.abyssallib.server.placeholder.Placeholder;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderArgument;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderContext;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderResult;
import net.kyori.adventure.key.Key;

public abstract class AbstractStringPlaceholder extends Placeholder<String> implements Expressionable<String> {

    public AbstractStringPlaceholder(Key id) {
        super(id, String.class);
    }

    @Override
    public PlaceholderResult<String> add(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<String> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        return PlaceholderResult.success(res.getValue() + other.asString());
    }

    @Override public PlaceholderResult<String> sub(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> mul(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> div(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> mod(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> pow(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> min(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> max(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    
    @Override public PlaceholderResult<String> sin(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> cos(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> tan(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> asin(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> acos(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> atan(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> round(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> floor(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> ceil(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<String> abs(PlaceholderContext ctx) { return PlaceholderResult.empty(); }

    @Override
    public PlaceholderResult<Boolean> eq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<String> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        return PlaceholderResult.success(res.getValue().equals(other.asString()));
    }

    @Override
    public PlaceholderResult<Boolean> neq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<String> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        return PlaceholderResult.success(!res.getValue().equals(other.asString()));
    }

    @Override public PlaceholderResult<Boolean> gt(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> lt(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> gte(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> lte(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }

    @Override public PlaceholderResult<Boolean> and(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> or(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> xor(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> not(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
}