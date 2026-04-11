package com.github.darksoulq.abyssallib.common.reflection;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<T> {

    private final T value;
    private final Throwable error;

    private Result(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public T get() {
        if (error != null) throw new RuntimeException(error);
        return value;
    }

    public T getOrNull() {
        return value;
    }

    public T getOrElse(T defaultValue) {
        return isSuccess() ? value : defaultValue;
    }

    public T getOrElse(Supplier<T> supplier) {
        return isSuccess() ? value : supplier.get();
    }

    public Throwable getError() {
        return error;
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }

    public Result<T> ifSuccess(Consumer<T> action) {
        if (isSuccess()) action.accept(value);
        return this;
    }

    public Result<T> ifFailure(Consumer<Throwable> action) {
        if (isFailure()) action.accept(error);
        return this;
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (isSuccess()) {
            try {
                return Result.success(mapper.apply(value));
            } catch (Throwable t) {
                return Result.failure(t);
            }
        }
        return Result.failure(error);
    }

    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (isSuccess()) {
            try {
                return mapper.apply(value);
            } catch (Throwable t) {
                return Result.failure(t);
            }
        }
        return Result.failure(error);
    }
}