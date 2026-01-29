package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import org.bukkit.Material

fun DeferredRegistry<Item>.register(
    id: String,
    material: Material,
    init: ItemBuilder.() -> Unit
): Holder<Item> {
    return this.register(id) { identifier ->
        val builder = ItemBuilder(identifier, material)
        builder.init()
        builder.build()
    }
}

fun DeferredRegistry<Item>.item(
    id: String,
    material: Material = Material.PAPER,
    init: ItemBuilder.() -> Unit = {}
): Holder<Item> = register(id, material, init)

fun item(
    namespace: String,
    path: String,
    material: Material,
    init: ItemBuilder.() -> Unit = {}
): Item {
    val builder = ItemBuilder(Identifier.of(namespace, path), material)
    builder.init()
    return builder.build()
}

fun item(
    id: Identifier,
    material: Material,
    init: ItemBuilder.() -> Unit = {}
): Item {
    val builder = ItemBuilder(id, material)
    builder.init()
    return builder.build()
}