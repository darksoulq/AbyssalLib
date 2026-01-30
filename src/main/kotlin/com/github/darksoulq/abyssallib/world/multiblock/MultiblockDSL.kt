package com.github.darksoulq.abyssallib.world.multiblock

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder

fun DeferredRegistry<Multiblock>.register(id: String, init: MultiblockBuilder.() -> Unit): Holder<Multiblock> {
    return this.register(id) { identifier ->
        val builder = MultiblockBuilder(identifier)
        builder.init()
        builder.build()
    }
}

fun DeferredRegistry<Multiblock>.multiblock(id: String, init: MultiblockBuilder.() -> Unit = {}): Holder<Multiblock> = register(id, init)

fun multiblock(namespace: String, path: String, init: MultiblockBuilder.() -> Unit = {}): Multiblock {
    val builder = MultiblockBuilder(Identifier.of(namespace, path))
    builder.init()
    return builder.build()
}

fun multiblock(id: Identifier, init: MultiblockBuilder.() -> Unit = {}): Multiblock {
    val builder = MultiblockBuilder(id)
    builder.init()
    return builder.build()
}