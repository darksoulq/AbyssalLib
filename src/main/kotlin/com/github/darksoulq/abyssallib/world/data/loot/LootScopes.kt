package com.github.darksoulq.abyssallib.world.data.loot

import org.bukkit.inventory.ItemStack
import java.util.ArrayList

@DslMarker
annotation class LootDSL

@LootDSL
class LootTableScope {
    var mergeStrategy: MergeStrategy = MergeStrategy.NONE
    var vanillaId: String? = null
    private val pools = ArrayList<LootPool>()

    fun pool(init: LootPoolScope.() -> Unit) {
        val scope = LootPoolScope()
        scope.init()
        pools.add(scope.build())
    }

    fun build(): LootTable {
        return LootTable(pools, mergeStrategy, vanillaId)
    }
}

@LootDSL
class LootPoolScope : Conditionable {
    var rolls: Int = 1
    var bonusRolls: Int = 0
    private val entries = ArrayList<LootEntry>()
    private val conditions = ArrayList<LootCondition>()

    fun item(stack: ItemStack, init: LootEntryScope.() -> Unit = {}) {
        val scope = LootEntryScope()
        scope.init()
        entries.add(LootEntry.ItemEntry(stack, scope.weight, scope.quality, scope.conditions, scope.functions))
    }

    fun empty(init: LootEntryScope.() -> Unit = {}) {
        val scope = LootEntryScope()
        scope.init()
        entries.add(LootEntry.EmptyEntry(scope.weight, scope.quality, scope.conditions, scope.functions))
    }

    override fun condition(condition: LootCondition) {
        conditions.add(condition)
    }

    fun build(): LootPool {
        return LootPool(rolls, bonusRolls, entries, conditions)
    }
}

@LootDSL
class LootEntryScope : Conditionable, Functionable {
    var weight: Int = 1
    var quality: Int = 0
    val conditions = ArrayList<LootCondition>()
    val functions = ArrayList<LootFunction>()

    override fun condition(condition: LootCondition) {
        conditions.add(condition)
    }

    override fun function(function: LootFunction) {
        functions.add(function)
    }
}