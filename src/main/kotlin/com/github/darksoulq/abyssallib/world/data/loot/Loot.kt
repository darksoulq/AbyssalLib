package com.github.darksoulq.abyssallib.world.data.loot

fun lootTable(init: LootTableScope.() -> Unit): LootTable {
    val scope = LootTableScope()
    scope.init()
    return scope.build()
}