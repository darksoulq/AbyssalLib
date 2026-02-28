package com.github.darksoulq.abyssallib.world.block

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import net.kyori.adventure.key.Key
import org.bukkit.Material

fun DeferredRegistry<CustomBlock>.register(
    id: String,
    material: Material,
    init: BlockBuilder.() -> Unit
): Holder<CustomBlock> {
    return this.register(id) { key ->
        val builder = BlockBuilder(key, material)
        builder.init()
        builder.build()
    }
}

fun DeferredRegistry<CustomBlock>.block(
    id: String,
    material: Material = Material.STONE,
    init: BlockBuilder.() -> Unit = {}
): Holder<CustomBlock> = register(id, material, init)

fun block(
    namespace: String,
    path: String,
    material: Material,
    init: BlockBuilder.() -> Unit = {}
): CustomBlock {
    val builder = BlockBuilder(Key.key(namespace, path), material)
    builder.init()
    return builder.build()
}

fun block(
    id: Key,
    material: Material,
    init: BlockBuilder.() -> Unit = {}
): CustomBlock {
    val builder = BlockBuilder(id, material)
    builder.init()
    return builder.build()
}