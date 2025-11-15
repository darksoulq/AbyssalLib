package com.github.darksoulq.abyssallib.world.data.loot

fun lootPool(rolls: Int, block: LootPoolBuilder.() -> Unit): LootPool {
    val builder = LootPoolBuilder(rolls)
    builder.block()
    return builder.build()
}

class LootPoolBuilder(private val rolls: Int) {
    private val entries = mutableListOf<LootEntry>()
    private val conditions = mutableListOf<LootCondition>()

    fun entry(entry: LootEntry) = apply {
        entries.add(entry)
    }

    fun condition(condition: LootCondition) = apply {
        conditions.add(condition)
    }

    fun build(): LootPool {
        val pool = LootPool(rolls)
        entries.forEach { pool.addEntry(it) }
        conditions.forEach { pool.`when`(it) }
        return pool
    }
}
