package com.github.darksoulq.abyssallib.common.util;

import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A monadic container type that represents a computation that may either result in a
 * successful value or a failure (exception).
 * <p>
 * This is useful for chaining operations that might throw checked exceptions without
 * nesting try-catch blocks.
 *
 * @param <T> The type of the successful value.
 */
public abstract class Try<T> {

    /**
     * Executes a supplier that may throw an exception and wraps the result in a Try.
     *
     * @param <T>      The result type.
     * @param supplier The computation to perform.
     * @return A Success containing the value, or a Failure containing the caught Throwable.
     */
    public static <T> Try<T> of(ThrowingSupplier<T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Executes a runnable that may throw an exception and wraps the result in a Try.
     *
     * @param runnable The action to perform.
     * @return A Success containing null if successful, or a Failure containing the caught Throwable.
     */
    public static Try<Void> run(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return new Success<>(null);
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * @return {@code true} if the computation completed without an exception.
     */
    public abstract boolean isSuccess();

    /**
     * Retrieves the successful value or throws a {@link RuntimeException} wrapping the original failure.
     *
     * @return The successful value.
     */
    public abstract T get();

    /**
     * Retrieves the exception that caused the failure.
     *
     * @return The {@link Throwable} caught during computation.
     * @throws NoSuchElementException If the Try is a Success.
     */
    public abstract Throwable getException();

    /**
     * Transforms the successful value using the provided mapper.
     * If the mapper throws an exception, the result is a Failure.
     *
     * @param <U>    The new result type.
     * @param mapper The function to apply to the successful value.
     * @return A new Try instance.
     */
    public abstract <U> Try<U> map(ThrowingFunction<? super T, ? extends U> mapper);

    /**
     * Transforms the successful value into another Try.
     *
     * @param <U>    The result type of the new Try.
     * @param mapper The function to apply.
     * @return The result of the mapper, or a Failure if an exception occurs.
     */
    public abstract <U> Try<U> flatMap(ThrowingFunction<? super T, Try<U>> mapper);

    /**
     * Executes the provided action if the computation failed.
     *
     * @param action The consumer to process the exception.
     * @return This Try instance for chaining.
     */
    public abstract Try<T> onFailure(Consumer<Throwable> action);

    /**
     * Executes the provided action if the computation succeeded.
     *
     * @param action The consumer to process the value.
     * @return This Try instance for chaining.
     */
    public abstract Try<T> onSuccess(Consumer<T> action);

    /**
     * Returns the successful value if present, otherwise returns the provided default.
     *
     * @param other The default value.
     * @return The successful value or the default.
     */
    public abstract T orElse(@Nullable T other);

    /**
     * Returns the successful value if present, otherwise returns the result of the supplier.
     *
     * @param other The supplier for the fallback value.
     * @return The successful value or the supplied default.
     */
    public abstract T orElseGet(Supplier<? extends T> other);

    /**
     * Returns the successful value or throws an exception provided by the mapper.
     *
     * @param <X>               The type of exception to throw.
     * @param exceptionProvider Function to convert the original failure into a new exception.
     * @return The successful value.
     * @throws X If the computation failed.
     */
    public abstract <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) throws X;

    /**
     * Converts this Try into an {@link Optional}. Failures result in an empty Optional.
     *
     * @return An Optional representation of the result.
     */
    public abstract Optional<T> toOptional();

    /**
     * Internal implementation of a successful computation.
     */
    private static class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override public boolean isSuccess() { return true; }
        @Override public T get() { return value; }
        @Override public Throwable getException() { throw new NoSuchElementException("Try is a Success, no exception available."); }

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

        @Override public Try<T> onFailure(Consumer<Throwable> action) { return this; }
        @Override public Try<T> onSuccess(Consumer<T> action) { action.accept(value); return this; }
        @Override public T orElse(T other) { return value; }
        @Override public T orElseGet(Supplier<? extends T> other) { return value; }
        @Override public <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) { return value; }
        @Override public Optional<T> toOptional() { return Optional.ofNullable(value); }
    }

    /**
     * Internal implementation of a failed computation.
     */
    private static class Failure<T> extends Try<T> {
        private final Throwable exception;

        private Failure(Throwable exception) {
            this.exception = exception;
        }

        @Override public boolean isSuccess() { return false; }
        @Override public T get() { throw new RuntimeException(exception); }
        @Override public Throwable getException() { return exception; }

        @Override @SuppressWarnings("unchecked")
        public <U> Try<U> map(ThrowingFunction<? super T, ? extends U> mapper) { return (Try<U>) this; }

        @Override @SuppressWarnings("unchecked")
        public <U> Try<U> flatMap(ThrowingFunction<? super T, Try<U>> mapper) { return (Try<U>) this; }

        @Override public Try<T> onFailure(Consumer<Throwable> action) { action.accept(exception); return this; }
        @Override public Try<T> onSuccess(Consumer<T> action) { return this; }
        @Override public T orElse(T other) { return other; }
        @Override public T orElseGet(Supplier<? extends T> other) { return other.get(); }
        @Override public <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) throws X { throw exceptionProvider.apply(exception); }
        @Override public Optional<T> toOptional() { return Optional.empty(); }
    }

    /** @param <T> Result type. */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        /** @return Result. @throws Throwable during execution. */
        T get() throws Throwable;
    }

    /** Functional interface for a runnable that can throw. */
    @FunctionalInterface
    public interface ThrowingRunnable {
        /** @throws Throwable during execution. */
        void run() throws Throwable;
    }

    /**
     * @param <T> Input type.
     * @param <R> Return type.
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        /** @param t input. @return Result. @throws Throwable during execution. */
        R apply(T t) throws Throwable;
    }
}