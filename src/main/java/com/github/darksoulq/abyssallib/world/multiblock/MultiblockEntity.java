package com.github.darksoulq.abyssallib.world.multiblock;

import com.github.darksoulq.abyssallib.world.entity.AbstractPropertyEntity;

public abstract class MultiblockEntity extends AbstractPropertyEntity<Multiblock> {

    public MultiblockEntity(Multiblock multiblock) {
        super(multiblock);
    }

    public Multiblock getMultiblock() {
        return getType();
    }
}