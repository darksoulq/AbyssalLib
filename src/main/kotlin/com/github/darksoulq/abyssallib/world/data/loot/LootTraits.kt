package com.github.darksoulq.abyssallib.world.data.loot

import com.github.darksoulq.abyssallib.world.data.loot.function.*
import net.kyori.adventure.text.Component

interface Conditionable {
    fun condition(condition: LootCondition)
}

interface Functionable {
    fun function(function: LootFunction)
}

fun Functionable.setName(name: Component) {
    function(SetNameFunction(name))
}

fun Functionable.setLore(lore: List<Component>) {
    function(SetLoreFunction(lore))
}

fun Functionable.setLore(vararg lore: Component) {
    function(SetLoreFunction(lore.toList()))
}

fun Functionable.setDamage(min: Float, max: Float) {
    function(SetDamageFunction(min, max))
}

fun Functionable.enchantRandomly(enchantments: List<String>) {
    function(EnchantRandomlyFunction(enchantments))
}

fun Functionable.enchantRandomly(vararg enchantments: String) {
    function(EnchantRandomlyFunction(enchantments.toList()))
}