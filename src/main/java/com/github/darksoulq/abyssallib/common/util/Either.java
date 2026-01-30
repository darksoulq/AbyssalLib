package com.github.darksoulq.abyssallib.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A container object which may hold one of two types of values: a Left or a Right.
 * <p>
 * By convention, the Left type represents a failure or error, while the Right type
 * represents a success or the "correct" value.
 *
 * @param <L> The type of the Left value.
 * @param <R> The type of the Right value.
 */
public abstract class Either<L, R> {

    /**
     * Private constructor to prevent external instantiation and maintain the algebraic data type.
     */
    private Either() {}

    /**
     * Creates an instance of Either containing a Left value.
     *
     * @param <L>   The type of the Left value.
     * @param <R>   The type of the Right value.
     * @param value The value to wrap.
     * @return A Left instance of Either.
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    /**
     * Creates an instance of Either containing a Right value.
     *
     * @param <L>   The type of the Left value.
     * @param <R>   The type of the Right value.
     * @param value The value to wrap.
     * @return A Right instance of Either.
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    /**
     * @return An Optional containing the Left value if present, otherwise empty.
     */
    public abstract Optional<L> left();

    /**
     * @return An Optional containing the Right value if present, otherwise empty.
     */
    public abstract Optional<R> right();

    /**
     * Returns the Left value if present, otherwise returns the provided default.
     *
     * @param def The default value.
     * @return The Left value or the default.
     */
    public abstract L leftOrElse(L def);

    /**
     * Returns the Right value if present, otherwise returns the provided default.
     *
     * @param def The default value.
     * @return The Right value or the default.
     */
    public abstract R rightOrElse(R def);

    /**
     * Returns the Left value or throws an exception if this is a Right.
     *
     * @return The Left value.
     * @throws IllegalStateException If this is a Right instance.
     */
    public abstract L leftOrThrow();

    /**
     * Returns the Right value or throws an exception if this is a Left.
     *
     * @return The Right value.
     * @throws IllegalStateException If this is a Left instance.
     */
    public abstract R rightOrThrow();

    /**
     * Transforms the Left value using the provided function.
     *
     * @param <LL> The new Left type.
     * @param f    The transformation function.
     * @return A new Either with the transformed Left or the original Right.
     */
    public abstract <LL> Either<LL, R> mapLeft(Function<? super L, ? extends LL> f);

    /**
     * Transforms the Right value using the provided function.
     *
     * @param <RR> The new Right type.
     * @param f    The transformation function.
     * @return A new Either with the transformed Right or the original Left.
     */
    public abstract <RR> Either<L, RR> mapRight(Function<? super R, ? extends RR> f);

    /**
     * Transforms both the Left and Right values using the provided functions.
     *
     * @param <LL> The new Left type.
     * @param <RR> The new Right type.
     * @param lf   The function to apply to a Left.
     * @param rf   The function to apply to a Right.
     * @return A new transformed Either.
     */
    @SuppressWarnings("unchecked")
    public <LL, RR> Either<LL, RR> map(Function<? super L, ? extends LL> lf,
                                       Function<? super R, ? extends RR> rf) {
        return ((Either<LL, R>) mapLeft(lf)).mapRight(rf);
    }

    /**
     * Executes the consumer if this is a Left.
     *
     * @param c The consumer to execute.
     */
    public abstract void ifLeft(Consumer<? super L> c);

    /**
     * Executes the consumer if this is a Right.
     *
     * @param c The consumer to execute.
     */
    public abstract void ifRight(Consumer<? super R> c);

    /**
     * Executes the appropriate consumer based on which side is present.
     *
     * @param lc The consumer for a Left value.
     * @param rc The consumer for a Right value.
     */
    public void apply(Consumer<? super L> lc, Consumer<? super R> rc) {
        ifLeft(lc);
        ifRight(rc);
    }

    /**
     * Reduces the Either to a single value by applying the matching function.
     *
     * @param <T> The result type.
     * @param lf  The function to apply to a Left.
     * @param rf  The function to apply to a Right.
     * @return The result of the applied function.
     */
    public abstract <T> T fold(Function<? super L, ? extends T> lf,
                               Function<? super R, ? extends T> rf);

    /**
     * FlatMaps both sides into a new Either type.
     *
     * @param <LL> The new Left type.
     * @param <RR> The new Right type.
     * @param lf   The function providing a new Either from a Left.
     * @param rf   The function providing a new Either from a Right.
     * @return The new Either instance.
     */
    @SuppressWarnings("unchecked")
    public <LL, RR> Either<LL, RR> flatMap(
        Function<? super L, ? extends Either<? extends LL, ? extends RR>> lf,
        Function<? super R, ? extends Either<? extends LL, ? extends RR>> rf) {
        return (Either<LL, RR>) fold(lf, rf);
    }

    /**
     * Swaps the sides of the Either.
     *
     * @return A new Either where the Left is the old Right and vice versa.
     */
    public Either<R, L> swap() {
        return fold(Either::right, Either::left);
    }

    /**
     * Represents the Left side of the {@link Either}.
     *
     * @param <L> The Left type.
     * @param <R> The Right type.
     */
    public static final class Left<L, R> extends Either<L, R> {
        /** The wrapped value. */
        private final L value;

        /**
         * @param value The non-null value to wrap.
         */
        public Left(L value) {
            this.value = Objects.requireNonNull(value);
        }

        /** @return The wrapped value. */
        public L value() {
            return value;
        }

        @Override
        public Optional<L> left() {
            return Optional.of(value);
        }

        @Override
        public Optional<R> right() {
            return Optional.empty();
        }

        @Override
        public L leftOrElse(L def) {
            return value;
        }

        @Override
        public R rightOrElse(R def) {
            return def;
        }

        @Override
        public L leftOrThrow() {
            return value;
        }

        @Override
        public R rightOrThrow() {
            throw new IllegalStateException("Right value requested from Left");
        }

        @Override
        public <LL> Either<LL, R> mapLeft(Function<? super L, ? extends LL> f) {
            return new Left<>(f.apply(value));
        }

        @Override
        public <RR> Either<L, RR> mapRight(Function<? super R, ? extends RR> f) {
            return new Left<>(value);
        }

        @Override
        public void ifLeft(Consumer<? super L> c) {
            c.accept(value);
        }

        @Override
        public void ifRight(Consumer<? super R> c) {}

        @Override
        public <T> T fold(Function<? super L, ? extends T> lf,
                          Function<? super R, ? extends T> rf) {
            return lf.apply(value);
        }

        @Override
        public String toString() {
            return "Left(" + value + ")";
        }
    }

    /**
     * Represents the Right side of the {@link Either}.
     *
     * @param <L> The Left type.
     * @param <R> The Right type.
     */
    public static final class Right<L, R> extends Either<L, R> {
        /** The wrapped value. */
        private final R value;

        /**
         * @param value The non-null value to wrap.
         */
        public Right(R value) {
            this.value = Objects.requireNonNull(value);
        }

        /** @return The wrapped value. */
        public R value() {
            return value;
        }

        @Override
        public Optional<L> left() {
            return Optional.empty();
        }

        @Override
        public Optional<R> right() {
            return Optional.of(value);
        }

        @Override
        public L leftOrElse(L def) {
            return def;
        }

        @Override
        public R rightOrElse(R def) {
            return value;
        }

        @Override
        public L leftOrThrow() {
            throw new IllegalStateException("Left value requested from Right");
        }

        @Override
        public R rightOrThrow() {
            return value;
        }

        @Override
        public <LL> Either<LL, R> mapLeft(Function<? super L, ? extends LL> f) {
            return new Right<>(value);
        }

        @Override
        public <RR> Either<L, RR> mapRight(Function<? super R, ? extends RR> f) {
            return new Right<>(f.apply(value));
        }

        @Override
        public void ifLeft(Consumer<? super L> c) {}

        @Override
        public void ifRight(Consumer<? super R> c) {
            c.accept(value);
        }

        @Override
        public <T> T fold(Function<? super L, ? extends T> lf,
                          Function<? super R, ? extends T> rf) {
            return rf.apply(value);
        }

        @Override
        public String toString() {
            return "Right(" + value + ")";
        }
    }
}