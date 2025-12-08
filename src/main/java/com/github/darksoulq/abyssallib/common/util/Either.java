package com.github.darksoulq.abyssallib.common.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Either<L, R> {

    private Either() {}

    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    public abstract Optional<L> left();
    public abstract Optional<R> right();

    public abstract L leftOrElse(L def);
    public abstract R rightOrElse(R def);

    public abstract L leftOrThrow();
    public abstract R rightOrThrow();

    public abstract <LL> Either<LL, R> mapLeft(Function<? super L, ? extends LL> f);
    public abstract <RR> Either<L, RR> mapRight(Function<? super R, ? extends RR> f);

    @SuppressWarnings("unchecked")
    public <LL, RR> Either<LL, RR> map(Function<? super L, ? extends LL> lf,
                                       Function<? super R, ? extends RR> rf) {
        return ((Either<LL, R>) mapLeft(lf)).mapRight(rf);
    }

    public abstract void ifLeft(Consumer<? super L> c);
    public abstract void ifRight(Consumer<? super R> c);

    public void apply(Consumer<? super L> lc, Consumer<? super R> rc) {
        ifLeft(lc);
        ifRight(rc);
    }

    public abstract <T> T fold(Function<? super L, ? extends T> lf,
                               Function<? super R, ?extends T> rf);

    @SuppressWarnings("unchecked")
    public <LL, RR> Either<LL, RR> flatMap(
            Function<? super L, ? extends Either<? extends LL, ? extends RR>> lf,
            Function<? super R, ? extends Either<? extends LL, ? extends RR>> rf) {
        return (Either<LL, RR>) fold(lf, rf);
    }

    public Either<R, L> swap() {
        return fold(Either::right, Either::left);
    }

    public static final class Left<L, R> extends Either<L, R> {
        private final L value;

        public Left(L value) {
            this.value = Objects.requireNonNull(value);
        }

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

    public static final class Right<L, R> extends Either<L, R> {
        private final R value;

        public Right(R value) {
            this.value = Objects.requireNonNull(value);
        }

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
