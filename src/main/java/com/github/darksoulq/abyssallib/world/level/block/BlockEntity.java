package com.github.darksoulq.abyssallib.world.level.block;

/**
 * Represents the entity data associated with a custom {@link Block}.
 * <p>
 * A {@code BlockEntity} stores additional state and behavior for a block that requires more than just
 * a material type, such as inventories, timers, or other dynamic data.
 * </p>
 * <p>
 * This is an abstract base class intended to be extended by specific block entity implementations.
 * </p>
 */
public abstract class BlockEntity {

    /**
     * The {@link Block} instance this entity is associated with.
     */
    private final Block block;

    /**
     * Constructs a new {@code BlockEntity} linked to the given {@link Block}.
     *
     * @param block the block this entity belongs to
     */
    public BlockEntity(Block block) {
        this.block = block;
    }

    /**
     * Returns the block that this entity is associated with.
     *
     * @return the associated {@link Block}
     */
    public Block getBlock() {
        return block;
    }

    /**
     * This method is called every tick
     */
    public void serverTick() {}

    /**
     * This method is called randomly similar to how minecraft ticks crops
     */
    public void randomTick() {}

    /**
     * Called after this entity is loaded from persistent storage (deserialized).
     * <p>
     * Subclasses can override this method to perform any necessary initialization
     * or data processing after loading.
     * </p>
     */
    public void onLoad() {}

    /**
     * Called before this entity is saved to persistent storage (serialized).
     * <p>
     * Subclasses can override this method to update or prepare data prior to saving.
     * </p>
     */
    public void onSave() {}
}
