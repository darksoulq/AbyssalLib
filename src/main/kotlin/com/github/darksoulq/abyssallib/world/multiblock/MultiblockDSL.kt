package com.github.darksoulq.abyssallib.world.multiblock

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import net.kyori.adventure.key.Key

fun DeferredRegistry<Multiblock>.register(id: String, init: MultiblockBuilder.() -> Unit): Holder<Multiblock> {
    return this.register(id) { key ->
        val builder = MultiblockBuilder(key)
        builder.init()
        builder.build()
    }
}

fun DeferredRegistry<Multiblock>.multiblock(id: String, init: MultiblockBuilder.() -> Unit = {}): Holder<Multiblock> = register(id, init)

fun multiblock(namespace: String, path: String, init: MultiblockBuilder.() -> Unit = {}): Multiblock {
    val builder = MultiblockBuilder(Key.key(namespace, path))
    builder.init()
    return builder.build()
}

fun multiblock(id: Key, init: MultiblockBuilder.() -> Unit = {}): Multiblock {
    val builder = MultiblockBuilder(id)
    builder.init()
    return builder.build()
}