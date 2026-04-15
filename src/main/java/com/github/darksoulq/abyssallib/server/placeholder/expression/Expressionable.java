package com.github.darksoulq.abyssallib.server.placeholder.expression;

import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderArgument;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderContext;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderResult;

public interface Expressionable<T> {
    PlaceholderResult<T> add(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> sub(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> mul(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> div(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> mod(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> pow(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> min(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<T> max(PlaceholderContext ctx, PlaceholderArgument other);

    PlaceholderResult<T> sin(PlaceholderContext ctx);
    PlaceholderResult<T> cos(PlaceholderContext ctx);
    PlaceholderResult<T> tan(PlaceholderContext ctx);
    PlaceholderResult<T> asin(PlaceholderContext ctx);
    PlaceholderResult<T> acos(PlaceholderContext ctx);
    PlaceholderResult<T> atan(PlaceholderContext ctx);
    PlaceholderResult<T> round(PlaceholderContext ctx);
    PlaceholderResult<T> floor(PlaceholderContext ctx);
    PlaceholderResult<T> ceil(PlaceholderContext ctx);
    PlaceholderResult<T> abs(PlaceholderContext ctx);

    PlaceholderResult<Boolean> eq(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> neq(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> gt(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> lt(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> gte(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> lte(PlaceholderContext ctx, PlaceholderArgument other);

    PlaceholderResult<Boolean> and(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> or(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> xor(PlaceholderContext ctx, PlaceholderArgument other);
    PlaceholderResult<Boolean> not(PlaceholderContext ctx);
}