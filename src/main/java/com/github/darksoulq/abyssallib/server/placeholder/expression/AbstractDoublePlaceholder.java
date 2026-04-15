package com.github.darksoulq.abyssallib.server.placeholder.expression;

import com.github.darksoulq.abyssallib.server.placeholder.Placeholder;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderArgument;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderContext;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderResult;
import net.kyori.adventure.key.Key;

public abstract class AbstractDoublePlaceholder extends Placeholder<Double> implements Expressionable<Double> {

    public AbstractDoublePlaceholder(Key id) {
        super(id, Double.class);
    }

    @Override
    public PlaceholderResult<Double> add(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() + val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Double> sub(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() - val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Double> mul(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() * val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Double> div(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        if (val == null || val == 0) return PlaceholderResult.error("Division by zero");
        return PlaceholderResult.success(res.getValue() / val);
    }

    @Override
    public PlaceholderResult<Double> mod(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        if (val == null || val == 0) return PlaceholderResult.error("Modulo by zero");
        return PlaceholderResult.success(res.getValue() % val);
    }

    @Override
    public PlaceholderResult<Double> pow(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(Math.pow(res.getValue(), val)) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Double> min(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(Math.min(res.getValue(), val)) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Double> max(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(Math.max(res.getValue(), val)) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Double> sin(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.sin(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> cos(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.cos(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> tan(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.tan(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> asin(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.asin(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> acos(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.acos(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> atan(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.atan(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> round(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success((double) Math.round(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> floor(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.floor(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> ceil(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.ceil(res.getValue()));
    }

    @Override
    public PlaceholderResult<Double> abs(PlaceholderContext ctx) {
        PlaceholderResult<Double> res = resolve(ctx);
        return res.isEmpty() || res.isError() ? res : PlaceholderResult.success(Math.abs(res.getValue()));
    }

    @Override
    public PlaceholderResult<Boolean> eq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue().equals(val)) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> neq(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(!res.getValue().equals(val)) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> gt(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() > val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> lt(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() < val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> gte(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() >= val) : PlaceholderResult.empty();
    }

    @Override
    public PlaceholderResult<Boolean> lte(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Double> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();
        Double val = other.asDouble().getOrNull();
        return val != null ? PlaceholderResult.success(res.getValue() <= val) : PlaceholderResult.empty();
    }

    @Override public PlaceholderResult<Boolean> and(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> or(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> xor(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Boolean> not(PlaceholderContext ctx) { return PlaceholderResult.empty(); }
}