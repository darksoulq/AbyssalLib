package com.github.darksoulq.abyssallib.world.block

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import org.bukkit.Material

fun DeferredRegistry<CustomBlock>.register(
    id: String,
    material: Material,
    init: BlockBuilder.() -> Unit
): Holder<CustomBlock> {
    return this.register(id) { identifier ->
        val builder = BlockBuilder(identifier, material)
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
    val builder = BlockBuilder(Identifier.of(namespace, path), material)
    builder.init()
    return builder.build()
}

fun block(
    id: Identifier,
    material: Material,
    init: BlockBuilder.() -> Unit = {}
): CustomBlock {
    val builder = BlockBuilder(id, material)
    builder.init()
    return builder.build()
}