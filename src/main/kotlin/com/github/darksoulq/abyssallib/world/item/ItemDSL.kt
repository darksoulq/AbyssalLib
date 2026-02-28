package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import net.kyori.adventure.key.Key
import org.bukkit.Material

fun DeferredRegistry<Item>.register(id: String, material: Material, init: ItemBuilder.() -> Unit): Item {
    return this.register(id) { key ->
        val builder = ItemBuilder(key, material)
        builder.init()
        builder.build()
    }
}

fun DeferredRegistry<Item>.item(id: String, material: Material = Material.PAPER, init: ItemBuilder.() -> Unit = {}): Item = register(id, material, init)

fun item(namespace: String, path: String, material: Material, init: ItemBuilder.() -> Unit = {}): Item {
    val builder = ItemBuilder(Key.key(namespace, path), material)
    builder.init()
    return builder.build()
}

fun item(id: Key, material: Material, init: ItemBuilder.() -> Unit = {}): Item {
    val builder = ItemBuilder(id, material)
    builder.init()
    return builder.build()
}