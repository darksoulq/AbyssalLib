package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.List;

/**
 * Utility container for fixed-size tuple codecs.
 */
public class TupleCodecs {

    /**
     * Codec for a two-element tuple.
     *
     * @param <A> First element type.
     * @param <B> Second element type.
     */
    public static class PairCodec<A, B> implements Codec<Codec.Pair<A, B>> {
        private final Codec<A> first;
        private final Codec<B> second;

        /**
         * Creates a pair codec.
         *
         * @param first  Codec for the first element.
         * @param second Codec for the second element.
         */
        public PairCodec(Codec<A> first, Codec<B> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <D> DataResult<Codec.Pair<A, B>> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("list", "unknown")))
                .flatMap(list -> {
                    if (list.size() < 2) return DataResult.error(DataError.indexOutOfBounds(1, list.size()));
                    return first.decode(ops, list.get(0)).prependPath("[0]").flatMap(a ->
                        second.decode(ops, list.get(1)).prependPath("[1]").map(b -> new Codec.Pair<>(a, b))
                    );
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Codec.Pair<A, B> value) {
            return first.encode(ops, value.first()).prependPath("[0]").flatMap(a ->
                second.encode(ops, value.second()).prependPath("[1]").map(b -> ops.createList(List.of(a, b)))
            );
        }

        @Override
        public String describe() {
            return "Pair[" + first.describe() + ", " + second.describe() + "]";
        }

        @Override
        public <R> R accept(CodecVisitor<R> visitor) {
            return visitor.visitTuple(List.of(first, second));
        }
    }

    /**
     * Codec for a three-element tuple.
     *
     * @param <A> First element type.
     * @param <B> Second element type.
     * @param <C> Third element type.
     */
    public static class Tuple3Codec<A, B, C> implements Codec<Codec.Tuple3<A, B, C>> {
        private final Codec<A> first;
        private final Codec<B> second;
        private final Codec<C> third;

        /**
         * Creates a three-element tuple codec.
         *
         * @param first  Codec for the first element.
         * @param second Codec for the second element.
         * @param third  Codec for the third element.
         */
        public Tuple3Codec(Codec<A> first, Codec<B> second, Codec<C> third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        @Override
        public <D> DataResult<Codec.Tuple3<A, B, C>> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("list", "unknown")))
                .flatMap(list -> {
                    if (list.size() < 3) return DataResult.error(DataError.indexOutOfBounds(2, list.size()));
                    return first.decode(ops, list.get(0)).prependPath("[0]").flatMap(a ->
                        second.decode(ops, list.get(1)).prependPath("[1]").flatMap(b ->
                            third.decode(ops, list.get(2)).prependPath("[2]").map(c -> new Codec.Tuple3<>(a, b, c))
                        )
                    );
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Codec.Tuple3<A, B, C> value) {
            return first.encode(ops, value.first()).prependPath("[0]").flatMap(a ->
                second.encode(ops, value.second()).prependPath("[1]").flatMap(b ->
                    third.encode(ops, value.third()).prependPath("[2]").map(c -> ops.createList(List.of(a, b, c)))
                )
            );
        }

        @Override
        public String describe() {
            return "Tuple3[" + first.describe() + ", " + second.describe() + ", " + third.describe() + "]";
        }

        @Override
        public <R> R accept(CodecVisitor<R> visitor) {
            return visitor.visitTuple(List.of(first, second, third));
        }
    }

    /**
     * Codec for a four-element tuple.
     *
     * @param <A>      First element type.
     * @param <B>      Second element type.
     * @param <C>      Third element type.
     * @param <D_TYPE> Fourth element type.
     */
    public static class Tuple4Codec<A, B, C, D_TYPE> implements Codec<Codec.Tuple4<A, B, C, D_TYPE>> {
        private final Codec<A> first;
        private final Codec<B> second;
        private final Codec<C> third;
        private final Codec<D_TYPE> fourth;

        /**
         * Creates a four-element tuple codec.
         *
         * @param first  Codec for the first element.
         * @param second Codec for the second element.
         * @param third  Codec for the third element.
         * @param fourth Codec for the fourth element.
         */
        public Tuple4Codec(Codec<A> first, Codec<B> second, Codec<C> third, Codec<D_TYPE> fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        @Override
        public <D> DataResult<Codec.Tuple4<A, B, C, D_TYPE>> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("list", "unknown")))
                .flatMap(list -> {
                    if (list.size() < 4) return DataResult.error(DataError.indexOutOfBounds(3, list.size()));
                    return first.decode(ops, list.get(0)).prependPath("[0]").flatMap(a ->
                        second.decode(ops, list.get(1)).prependPath("[1]").flatMap(b ->
                            third.decode(ops, list.get(2)).prependPath("[2]").flatMap(c ->
                                fourth.decode(ops, list.get(3)).prependPath("[3]").map(d -> new Codec.Tuple4<>(a, b, c, d))
                            )
                        )
                    );
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Codec.Tuple4<A, B, C, D_TYPE> value) {
            return first.encode(ops, value.first()).prependPath("[0]").flatMap(a ->
                second.encode(ops, value.second()).prependPath("[1]").flatMap(b ->
                    third.encode(ops, value.third()).prependPath("[2]").flatMap(c ->
                        fourth.encode(ops, value.fourth()).prependPath("[3]").map(d -> ops.createList(List.of(a, b, c, d)))
                    )
                )
            );
        }

        @Override
        public String describe() {
            return "Tuple4[" + first.describe() + ", " + second.describe() + ", " + third.describe() + ", " + fourth.describe() + "]";
        }

        @Override
        public <R> R accept(CodecVisitor<R> visitor) {
            return visitor.visitTuple(List.of(first, second, third, fourth));
        }
    }
}