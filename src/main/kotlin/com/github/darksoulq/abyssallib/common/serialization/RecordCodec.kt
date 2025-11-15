package com.github.darksoulq.abyssallib.common.serialization

import kotlin.reflect.KProperty1

/**
 * Kotlin DSL for building RecordCodecBuilder codecs.
 *
 * Example usage:
 *
 * val COLOR = recordCodec<Color> {
 *     field(::alpha, Codecs.INT)
 *     field(::red, Codecs.INT)
 *     field(::green, Codecs.INT)
 *     field(::blue, Codecs.INT)
 *     build(::fromARGB)
 * }
 */
class RecordCodec<T> {

    private val fields = mutableListOf<Codec.Field<T, *>>()

    /**
     * Adds a field with an explicit Codec (mandatory).
     */
    fun <A> field(prop: KProperty1<T, A>, codec: Codec<A>) {
        fields.add(Codec.Field(prop.name, codec, prop::get))
    }

    fun <A1, A2, T2 : T> build(
        constructor: (A1, A2) -> T2
    ): Codec<T2> {
        require(fields.size == 2) { "Expected exactly 2 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            constructor
        )
    }

    fun <A1, A2, A3, T2 : T> build(
        constructor: (A1, A2, A3) -> T2
    ): Codec<T2> {
        require(fields.size == 3) { "Expected exactly 3 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, T2 : T> build(
        constructor: (A1, A2, A3, A4) -> T2
    ): Codec<T2> {
        require(fields.size == 4) { "Expected exactly 4 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5) -> T2
    ): Codec<T2> {
        require(fields.size == 5) { "Expected exactly 5 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6) -> T2
    ): Codec<T2> {
        require(fields.size == 6) { "Expected exactly 6 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7) -> T2
    ): Codec<T2> {
        require(fields.size == 7) { "Expected exactly 7 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8) -> T2
    ): Codec<T2> {
        require(fields.size == 8) { "Expected exactly 8 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9) -> T2
    ): Codec<T2> {
        require(fields.size == 9) { "Expected exactly 9 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) -> T2
    ): Codec<T2> {
        require(fields.size == 10) { "Expected exactly 10 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) -> T2
    ): Codec<T2> {
        require(fields.size == 11) { "Expected exactly 11 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) -> T2
    ): Codec<T2> {
        require(fields.size == 12) { "Expected exactly 12 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) -> T2
    ): Codec<T2> {
        require(fields.size == 13) { "Expected exactly 13 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) -> T2
    ): Codec<T2> {
        require(fields.size == 14) { "Expected exactly 14 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) -> T2
    ): Codec<T2> {
        require(fields.size == 15) { "Expected exactly 15 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            fields[14] as Codec.Field<T2, A15>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) -> T2
    ): Codec<T2> {
        require(fields.size == 16) { "Expected exactly 16 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            fields[14] as Codec.Field<T2, A15>,
            fields[15] as Codec.Field<T2, A16>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) -> T2
    ): Codec<T2> {
        require(fields.size == 17) { "Expected exactly 17 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            fields[14] as Codec.Field<T2, A15>,
            fields[15] as Codec.Field<T2, A16>,
            fields[16] as Codec.Field<T2, A17>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) -> T2
    ): Codec<T2> {
        require(fields.size == 18) { "Expected exactly 18 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            fields[14] as Codec.Field<T2, A15>,
            fields[15] as Codec.Field<T2, A16>,
            fields[16] as Codec.Field<T2, A17>,
            fields[17] as Codec.Field<T2, A18>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) -> T2
    ): Codec<T2> {
        require(fields.size == 19) { "Expected exactly 19 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            fields[14] as Codec.Field<T2, A15>,
            fields[15] as Codec.Field<T2, A16>,
            fields[16] as Codec.Field<T2, A17>,
            fields[17] as Codec.Field<T2, A18>,
            fields[18] as Codec.Field<T2, A19>,
            constructor
        )
    }

    fun <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, T2 : T> build(
        constructor: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) -> T2
    ): Codec<T2> {
        require(fields.size == 20) { "Expected exactly 20 fields" }
        @Suppress("UNCHECKED_CAST")
        return RecordCodecBuilder.create(
            fields[0] as Codec.Field<T2, A1>,
            fields[1] as Codec.Field<T2, A2>,
            fields[2] as Codec.Field<T2, A3>,
            fields[3] as Codec.Field<T2, A4>,
            fields[4] as Codec.Field<T2, A5>,
            fields[5] as Codec.Field<T2, A6>,
            fields[6] as Codec.Field<T2, A7>,
            fields[7] as Codec.Field<T2, A8>,
            fields[8] as Codec.Field<T2, A9>,
            fields[9] as Codec.Field<T2, A10>,
            fields[10] as Codec.Field<T2, A11>,
            fields[11] as Codec.Field<T2, A12>,
            fields[12] as Codec.Field<T2, A13>,
            fields[13] as Codec.Field<T2, A14>,
            fields[14] as Codec.Field<T2, A15>,
            fields[15] as Codec.Field<T2, A16>,
            fields[16] as Codec.Field<T2, A17>,
            fields[17] as Codec.Field<T2, A18>,
            fields[18] as Codec.Field<T2, A19>,
            fields[19] as Codec.Field<T2, A20>,
            constructor
        )
    }

}

/**
 * Entry point for the Kotlin DSL.
 */
fun <T> recordCodec(block: RecordCodec<T>.() -> Codec<T>): Codec<T> {
    val dsl = RecordCodec<T>()
    return dsl.block()
}
