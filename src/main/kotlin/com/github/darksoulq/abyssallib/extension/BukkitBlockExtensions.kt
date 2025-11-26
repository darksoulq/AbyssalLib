package com.github.darksoulq.abyssallib.extension

import com.github.darksoulq.abyssallib.world.util.BlockPersistentData
import org.bukkit.block.Block
import org.bukkit.persistence.PersistentDataContainer

fun Block.getPDC() : PersistentDataContainer {
    return BlockPersistentData.get(this)
}