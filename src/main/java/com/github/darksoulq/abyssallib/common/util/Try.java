package com.github.darksoulq.abyssallib.common.util;

import org.jetbrains.annotations.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A monadic container type that represents a computation that may either result in a
 * successful value or a failure containing an exception.
 *
 * @param <T> The type of the successful value.
 */
public abstract class Try<T> {

    /**
     * Executes a supplier that may throw an exception and wraps the result in a Try.
     *
     * @param <T> The result type of the supplier.
     * @param supplier The computation to perform which may throw a Throwable.
     * @return A Success containing the value if successful, or a Failure containing the caught Throwable.
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
     * @param runnable The action to perform which may throw a Throwable.
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
     * Determines if the computation completed successfully.
     *
     * @return True if the instance is a Success, false if it is a Failure.
     */
    public abstract boolean isSuccess();

    /**
     * Retrieves the successful value or throws a RuntimeException wrapping the failure.
     *
     * @return The successful value of type T.
     */
    public abstract T get();

    /**
     * Retrieves the exception that caused the failure.
     *
     * @return The Throwable caught during the computation.
     * @throws NoSuchElementException If the Try is a Success and contains no exception.
     */
    public abstract Throwable getException();

    /**
     * Transforms the successful value using the provided throwing mapper function.
     *
     * @param <U> The new result type after mapping.
     * @param mapper The function to apply to the successful value.
     * @return A new Success instance if mapping succeeds, or a Failure if an exception is caught.
     */
    public abstract <U> Try<U> map(ThrowingFunction<? super T, ? extends U> mapper);

    /**
     * Transforms the successful value into another Try using a throwing mapper function.
     *
     * @param <U> The result type of the new Try.
     * @param mapper The function to apply which returns a Try.
     * @return The result of the mapper, or a Failure if an exception occurs during execution.
     */
    public abstract <U> Try<U> flatMap(ThrowingFunction<? super T, Try<U>> mapper);

    /**
     * Executes the provided consumer action if the computation resulted in a failure.
     *
     * @param action The consumer to process the caught Throwable.
     * @return The current Try instance for method chaining.
     */
    public abstract Try<T> onFailure(Consumer<Throwable> action);

    /**
     * Executes the provided consumer action if the computation was successful.
     *
     * @param action The consumer to process the successful value.
     * @return The current Try instance for method chaining.
     */
    public abstract Try<T> onSuccess(Consumer<T> action);

    /**
     * Returns the successful value if present, otherwise returns the specified default.
     *
     * @param other The fallback value to return on failure.
     * @return The successful value or the provided default.
     */
    public abstract T orElse(@Nullable T other);

    /**
     * Returns the successful value if present, otherwise invokes the supplier for a fallback.
     *
     * @param other The supplier used to provide a default value on failure.
     * @return The successful value or the supplied default.
     */
    public abstract T orElseGet(Supplier<? extends T> other);

    /**
     * Returns the successful value or throws a transformed exception.
     *
     * @param <X> The specific type of Throwable to be thrown.
     * @param exceptionProvider Function to convert the original Throwable into type X.
     * @return The successful value.
     * @throws X If the computation failed.
     */
    public abstract <X extends Throwable> T orElseThrow(Function<Throwable, X> exceptionProvider) throws X;

    /**
     * Converts this Try instance into a standard Java Optional.
     *
     * @return An Optional containing the value if successful, or an empty Optional on failure.
     */
    public abstract Optional<T> toOptional();

    /**
     * Internal implementation representing a successful computation.
     *
     * @param <T> The type of the value held.
     */
    private static class Success<T> extends Try<T> {
        /**
         * The value produced by the computation.
         */
        private final T value;

        /**
         * Constructs a Success instance with the specified value.
         *
         * @param value The successful result.
         */
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
     * Internal implementation representing a failed computation.
     *
     * @param <T> The expected type of the missing value.
     */
    private static class Failure<T> extends Try<T> {
        /**
         * The exception caught during execution.
         */
        private final Throwable exception;

        /**
         * Constructs a Failure instance with the specified exception.
         *
         * @param exception The caught error.
         */
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

    /**
     * A functional interface for a supplier that may throw a checked or unchecked exception.
     *
     * @param <T> The type of the result.
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        /**
         * Retrieves the result.
         *
         * @return The result value.
         * @throws Throwable If the computation fails.
         */
        T get() throws Throwable;
    }

    /**
     * A functional interface for a runnable task that may throw a checked or unchecked exception.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        /**
         * Executes the task.
         *
         * @throws Throwable If the task execution fails.
         */
        void run() throws Throwable;
    }

    /**
     * A functional interface for a function that maps an input to an output and may throw an exception.
     *
     * @param <T> The input type.
     * @param <R> The result type.
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        /**
         * Applies the function to the given input.
         *
         * @param t The input value.
         * @return The mapped result.
         * @throws Throwable If the mapping logic fails.
         */
        R apply(T t) throws Throwable;
    }
}