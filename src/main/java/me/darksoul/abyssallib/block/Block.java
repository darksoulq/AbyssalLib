package me.darksoul.abyssallib.block;

import me.darksoul.abyssallib.event.context.block.BlockBreakContext;
import me.darksoul.abyssallib.event.context.block.BlockInteractContext;
import me.darksoul.abyssallib.event.context.block.BlockPlaceContext;
import me.darksoul.abyssallib.item.Item;
import me.darksoul.abyssallib.loot.LootTable;
import me.darksoul.abyssallib.util.ResourceLocation;
import org.bukkit.entity.LivingEntity;

/**
 * Represents a block in the game world. This class contains the block's
 * properties, such as its ID, data, and various events associated with
 * the block's interactions and actions, including placement, breaking,
 * interaction, stepping, and explosions.
 */
public class Block implements Cloneable {
    /**
     * The unique ID used to identify this block.
     */
    protected final ResourceLocation id;
    /**
     * The current data associated with this block.
     */
    private BlockData data = new BlockData();

    /**
     * Constructs a block with the given ID.
     *
     * @param id The {@link ResourceLocation} representing the block's ID.
     */
    public Block(ResourceLocation id) {
        this.id = id;
    }

    /**
     * Retrieves the block's unique ID.
     *
     * @return The {@link ResourceLocation} representing the block's ID.
     */
    public ResourceLocation id() {
        return id;
    }

    /**
     * Retrieves the item associated with the block. By default, returns {@code null}.
     *
     * @return The {@link Item} associated with the block, or {@code null}.
     */
    public Item blockItem() { return null; }

    /**
     * Places the block at the given location, updating the world state and triggering the
     * block's placement event.
     *
     * @param ctx The {@link BlockPlaceContext} containing the context of the block placement.
     */
    public void place(BlockPlaceContext ctx) {
        BlockManager.INSTANCE.setBlockAt(ctx.block.getLocation(), this);
        onPlace(ctx);
    }

    /**
     * Retrieves the loot table associated with the block. By default, returns {@code null}.
     *
     * @return The {@link LootTable} associated with the block, or {@code null}.
     */
    public LootTable lootTable() {
        return null;
    }
    /**
     * Retrieves the experience value dropped when the block is broken. By default, returns {@code 0}.
     *
     * @return The experience value dropped by the block, or {@code 0}.
     */
    public int exp() {
        return 0;
    }
    /**
     * Called when the block is placed in the world.
     *
     * @param ctx The {@link BlockPlaceContext} containing the context of the block placement.
     */
    public void onPlace(BlockPlaceContext ctx) {}
    /**
     * Called when the block is broken in the world.
     *
     * @param ctx The {@link BlockBreakContext} containing the context of the block break.
     */
    public void onBreak(BlockBreakContext ctx) {}
    /**
     * Called when a player or entity interacts with the block.
     *
     * @param ctx The {@link BlockInteractContext} containing the context of the block interaction.
     */
    public void onInteract(BlockInteractContext ctx) {}
    /**
     * Called when an entity steps on the block.
     *
     * @param entity The {@link LivingEntity} that stepped on the block.
     */
    public void onStep(LivingEntity entity) {}
    /**
     * Called when the block is affected by an explosion.
     */
    public void onExplode() {}
    /**
     * Called when a projectile hits the block.
     */
    public void onProjectileHit() {}

    /**
     * Retrieves the block's data.
     *
     * @return The {@link BlockData} associated with the block.
     */
    public BlockData getData() {
        return data;
    }
    /**
     * Sets the block's data.
     *
     * @param data The {@link BlockData} to associate with the block.
     */
    public void setData(BlockData data) {
        this.data = data;
    }

    /**
     * Checks whether the given Bukkit {@link org.bukkit.block.Block} is a custom block
     * managed by AbyssalLib. If it is, returns the corresponding {@link Block} instance.
     * If not, returns {@code null}.
     *
     * @param bukkitBlock The Bukkit block to check.
     * @return The associated custom {@link Block}, or {@code null} if it is not custom.
     */
    public static Block from(org.bukkit.block.Block bukkitBlock) {
        if (bukkitBlock == null) return null;
        return BlockManager.INSTANCE.getBlockAt(bukkitBlock.getLocation());
    }

}
