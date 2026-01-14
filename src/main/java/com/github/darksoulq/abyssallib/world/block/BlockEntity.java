package com.github.darksoulq.abyssallib.world.block;

import com.github.darksoulq.abyssallib.world.entity.AbstractPropertyEntity;

/**
 * Represents the entity data associated with a custom {@link CustomBlock}.
 */
public abstract class BlockEntity extends AbstractPropertyEntity<CustomBlock> {

    /**
     * Constructs a new {@code BlockEntity} linked to the given {@link CustomBlock}.
     *
     * @param block the block this entity belongs to
     */
    public BlockEntity(CustomBlock block) {
        super(block);
    }

    /**
     * Returns the block that this entity is associated with.
     *
     * @return the associated {@link CustomBlock}
     */
    public CustomBlock getBlock() {
        return getType();
    }
}