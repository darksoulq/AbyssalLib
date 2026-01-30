package com.github.darksoulq.abyssallib.world.entity

import com.github.darksoulq.abyssallib.world.block.BlockEntity
import com.github.darksoulq.abyssallib.world.block.CustomBlock
import com.github.darksoulq.abyssallib.world.multiblock.Multiblock
import com.github.darksoulq.abyssallib.world.multiblock.MultiblockEntity

fun blockEntity(init: EntityBuilder<CustomBlock, BlockEntity>.() -> Unit): EntityBuilder<CustomBlock, BlockEntity> {
    val builder = EntityBuilder<CustomBlock, BlockEntity>()
    builder.init()
    return builder
}

fun multiblockEntity(init: EntityBuilder<Multiblock, MultiblockEntity>.() -> Unit): EntityBuilder<Multiblock, MultiblockEntity> {
    val builder = EntityBuilder<Multiblock, MultiblockEntity>()
    builder.init()
    return builder
}