package com.github.darksoulq.abyssallib.world.data.loot

fun lootTable(block: LootTableBuilder.() -> Unit): LootTable {
    val builder = LootTableBuilder()
    builder.block()
    return builder.build()
}

class LootTableBuilder {
    private val pools = mutableListOf<LootPool>()

    fun pool(rolls: Int, block: LootPoolBuilder.() -> Unit) = apply {
        val poolBuilder = LootPoolBuilder(rolls)
        poolBuilder.block()
        pools.add(poolBuilder.build())
    }

    fun build(): LootTable {
        val table = LootTable()
        pools.forEach { table.addPool(it) }
        return table
    }
}
