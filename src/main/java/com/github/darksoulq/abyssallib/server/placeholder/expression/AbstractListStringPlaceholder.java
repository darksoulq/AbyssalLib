package com.github.darksoulq.abyssallib.server.placeholder.expression;

import com.github.darksoulq.abyssallib.server.placeholder.Placeholder;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderArgument;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderContext;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderResult;
import net.kyori.adventure.key.Key;

import java.util.List;

public abstract class AbstractListStringPlaceholder extends Placeholder<List<String>> implements Expressionable<List<String>> {

    @SuppressWarnings("unchecked")
    public AbstractListStringPlaceholder(Key id) {
        super(id, (Class<List<String>>) (Class<?>) List.class);
    }

    @Override
    public PlaceholderResult<List<String>> add(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> sub(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> mul(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> div(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> mod(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> pow(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> min(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> max(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> sin(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> cos(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> tan(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> asin(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> acos(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> atan(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> round(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> floor(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> ceil(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<List<String>> abs(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> eq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<List<String>> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        return PlaceholderResult.success(res.getValue().toString().equals(other.asString()));
    }

    @Override
    public PlaceholderResult<Boolean> neq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<List<String>> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        return PlaceholderResult.success(!res.getValue().toString().equals(other.asString()));
    }

    @Override
    public PlaceholderResult<Boolean> gt(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> lt(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> gte(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> lte(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> and(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> or(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> xor(PlaceholderContext ctx, PlaceholderArgument other) {
        return PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> not(PlaceholderContext ctx) {
        return PlaceholderResult.empty();
    }
}