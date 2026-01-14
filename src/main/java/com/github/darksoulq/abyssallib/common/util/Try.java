package com.github.darksoulq.abyssallib.common.util;

import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Try<T> {

    public static <T> Try<T> of(ThrowingSupplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    public static Try<Void> run(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return new Success<>(null);
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    public abstract boolean isSuccess();
    public abstract T get();
    public abstract Throwable getException();

    public abstract <U> Try<U> map(ThrowingFunction<? super T, ? extends U> mapper);
    public abstract <U> Try<U> flatMap(ThrowingFunction<? super T, Try<U>> mapper);

    public abstract Try<T> onFailure(Consumer<Throwable> action);
    public abstract Try<T> onSuccess(Consumer<T> action);

    public abstract T orElse(@Nullable T other);
    public abstract T orElseGet(Supplier<? extends T> other);
    public abstract <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) throws X;

    public abstract Optional<T> toOptional();

    private static class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getException() {
            throw new NoSuchElementException("Try is a Success, no exception available.");
        }

        @Override
        public <U> Try<U> map(ThrowingFunction<? super T, ? extends U> mapper) {
            try {
                return new Success<>(mapper.apply(value));
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        }

        @Override
        public <U> Try<U> flatMap(ThrowingFunction<? super T, Try<U>> mapper) {
            try {
                return mapper.apply(value);
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        }

        @Override
        public Try<T> onFailure(Consumer<Throwable> action) {
            return this;
        }

        @Override
        public Try<T> onSuccess(Consumer<T> action) {
            action.accept(value);
            return this;
        }

        @Override
        public T orElse(T other) {
            return value;
        }

        @Override
        public T orElseGet(Supplier<? extends T> other) {
            return value;
        }

        @Override
        public <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) {
            return value;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(value);
        }
    }

    private static class Failure<T> extends Try<T> {
        private final Throwable exception;

        private Failure(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() {
            throw new RuntimeException(exception);
        }

        @Override
        public Throwable getException() {
            return exception;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Try<U> map(ThrowingFunction<? super T, ? extends U> mapper) {
            return (Try<U>) this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Try<U> flatMap(ThrowingFunction<? super T, Try<U>> mapper) {
            return (Try<U>) this;
        }

        @Override
        public Try<T> onFailure(Consumer<Throwable> action) {
            action.accept(exception);
            return this;
        }

        @Override
        public Try<T> onSuccess(Consumer<T> action) {
            return this;
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public T orElseGet(Supplier<? extends T> other) {
            return other.get();
        }

        @Override
        public <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) throws X {
            throw exceptionProvider.apply(exception);
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Throwable;
    }
}