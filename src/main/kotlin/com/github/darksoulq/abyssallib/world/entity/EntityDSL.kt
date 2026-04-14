package com.github.darksoulq.abyssallib.world.entity

import com.github.darksoulq.abyssallib.world.block.BlockEntity
import com.github.darksoulq.abyssallib.world.block.CustomBlock

fun blockEntity(init: EntityBuilder<CustomBlock, BlockEntity>.() -> Unit): EntityBuilder<CustomBlock, BlockEntity> {
    val builder = EntityBuilder<CustomBlock, BlockEntity>()
    builder.init()
    return builder
}