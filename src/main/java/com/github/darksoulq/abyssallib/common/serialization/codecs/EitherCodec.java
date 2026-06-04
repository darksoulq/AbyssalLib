package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Either;

/**
 * Codec representing a value that may be decoded by either of two codecs.
 *
 * @param <A> Left value type.
 * @param <B> Right value type.
 */
public class EitherCodec<A, B> implements Codec<Either<A, B>> {
    private final Codec<A> left;
    private final Codec<B> right;

    /**
     * Creates an either codec.
     *
     * @param left Left branch codec.
     * @param right Right branch codec.
     */
    public EitherCodec(Codec<A> left, Codec<B> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <D> DataResult<Either<A, B>> decode(DynamicOps<D> ops, D input) {
        DataResult<A> leftRes = left.decode(ops, input);
        if (leftRes.isSuccess()) return leftRes.map(Either.Left::new);

        DataResult<B> rightRes = right.decode(ops, input);
        if (rightRes.isSuccess()) return rightRes.map(Either.Right::new);

        return DataResult.error(DataError.custom("Either codec failed. Left error: " + leftRes.error().orElse("") +
            ". Right error: " + rightRes.error().orElse("")));
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, Either<A, B> either) {
        if (either instanceof Either.Left<A, B> l) {
            return left.encode(ops, l.value());
        } else {
            return right.encode(ops, ((Either.Right<A, B>) either).value());
        }
    }

    @Override
    public String describe() {
        return "Either[" + left.describe() + " | " + right.describe() + "]";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitEither(left, right);
    }
}