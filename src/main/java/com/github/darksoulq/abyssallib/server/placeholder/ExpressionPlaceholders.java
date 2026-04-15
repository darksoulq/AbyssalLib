package com.github.darksoulq.abyssallib.server.placeholder;

import com.github.darksoulq.abyssallib.server.placeholder.expression.Expressionable;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;

public class ExpressionPlaceholders {

    public static final DeferredRegistry<Placeholder<?>> PLACEHOLDERS = DeferredRegistry.create(Registries.PLACEHOLDERS, "abyssallib");

    @SuppressWarnings("unchecked")
    private static <T> PlaceholderResult<T> evaluate(PlaceholderContext context, Operator<T> operator) {
        if (context.argsCount() < 1) return PlaceholderResult.empty();
        Placeholder<?> p = Registries.PLACEHOLDERS.get(context.getRaw(0, ""));
        if (p instanceof Expressionable expr) {
            return operator.apply((Expressionable<T>) expr, context);
        }
        return PlaceholderResult.empty();
    }

    private interface Operator<T> {
        PlaceholderResult<T> apply(Expressionable<T> expr, PlaceholderContext ctx);
    }

    public static final Placeholder<?> ADD = PLACEHOLDERS.register("add", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.add(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> SUB = PLACEHOLDERS.register("sub", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.sub(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> MUL = PLACEHOLDERS.register("mul", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.mul(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> DIV = PLACEHOLDERS.register("div", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.div(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> MOD = PLACEHOLDERS.register("mod", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.mod(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> POW = PLACEHOLDERS.register("pow", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.pow(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> MIN = PLACEHOLDERS.register("min", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.min(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> MAX = PLACEHOLDERS.register("max", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.max(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> SIN = PLACEHOLDERS.register("sin", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::sin);
        }
    });

    public static final Placeholder<?> COS = PLACEHOLDERS.register("cos", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::cos);
        }
    });

    public static final Placeholder<?> TAN = PLACEHOLDERS.register("tan", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::tan);
        }
    });

    public static final Placeholder<?> ASIN = PLACEHOLDERS.register("asin", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::asin);
        }
    });

    public static final Placeholder<?> ACOS = PLACEHOLDERS.register("acos", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::acos);
        }
    });

    public static final Placeholder<?> ATAN = PLACEHOLDERS.register("atan", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::atan);
        }
    });

    public static final Placeholder<?> ROUND = PLACEHOLDERS.register("round", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::round);
        }
    });

    public static final Placeholder<?> FLOOR = PLACEHOLDERS.register("floor", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::floor);
        }
    });

    public static final Placeholder<?> CEIL = PLACEHOLDERS.register("ceil", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::ceil);
        }
    });

    public static final Placeholder<?> ABS = PLACEHOLDERS.register("abs", id -> new Placeholder<>(id, Object.class) {
        @Override public PlaceholderResult<Object> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::abs);
        }
    });

    public static final Placeholder<?> EQ = PLACEHOLDERS.register("eq", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.eq(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> NEQ = PLACEHOLDERS.register("neq", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.neq(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> GT = PLACEHOLDERS.register("gt", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.gt(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> LT = PLACEHOLDERS.register("lt", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.lt(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> GTE = PLACEHOLDERS.register("gte", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.gte(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> LTE = PLACEHOLDERS.register("lte", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.lte(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> AND = PLACEHOLDERS.register("and", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.and(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> OR = PLACEHOLDERS.register("or", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.or(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> XOR = PLACEHOLDERS.register("xor", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, (expr, ctx) -> expr.xor(ctx, ctx.arg(1)));
        }
    });

    public static final Placeholder<?> NOT = PLACEHOLDERS.register("not", id -> new Placeholder<>(id, Boolean.class) {
        @Override public PlaceholderResult<Boolean> resolve(PlaceholderContext context) {
            return evaluate(context, Expressionable::not);
        }
    });
}