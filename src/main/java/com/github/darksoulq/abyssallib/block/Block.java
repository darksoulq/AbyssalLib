package com.github.darksoulq.abyssallib.block;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.event.context.block.BlockBreakContext;
import com.github.darksoulq.abyssallib.event.context.block.BlockInteractContext;
import com.github.darksoulq.abyssallib.event.context.block.BlockPlaceContext;
import com.github.darksoulq.abyssallib.event.context.block.ExplodeContext;
import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.loot.LootTable;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.tag.BlockTag;
import com.github.darksoulq.abyssallib.tag.ItemTag;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import java.util.function.Supplier;

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
    /** The Bukkit material representing this block's physical appearance. */
    private final Material material;
    /**
     * The current data associated with this block.
     */
    private BlockData data = new BlockData();

    /**
     * Constructs a block with the given ID.
     *
     * @param id The {@link ResourceLocation} representing the block's ID.
     * @param material The {@link Material} of the block (material must be a placeable block)
     */
    public Block(ResourceLocation id, Material material) {
        this.id = id;
        this.material = material;
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
     * Gets the material used for rendering and placement in the world.
     *
     * @return The Bukkit {@link Material} of the block.
     */
    public Material material() {
        return material;
    }

    /**
     * Whether to automatically generate a corresponding item for this block.
     *
     * @return {@code true} if an item should be generated, {@code false} otherwise.
     */
    public boolean generateItem() { return false; }

    /**
     * Gets a supplier that returns the item representation of this block.
     *
     * @return A {@link Supplier} of {@link Item}.
     */
    public Supplier<Item> item() {
        return () -> new Item(id, material);
    }

    /**
     * Places the block at the given location, updating the world state and triggering the
     * block's placement event.
     *
     * @param ctx The {@link BlockPlaceContext} containing the context of the block placement.
     */
    public void place(BlockPlaceContext ctx) {
        if (!material.isBlock()) {
            AbyssalLib.getInstance().getLogger().severe("Material is not a block; block id: " + id.toString());
            return;
        }
        BlockManager.INSTANCE.setBlockAt(ctx.bukkitBlock.getLocation(), this);
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
    public void onExplode(ExplodeContext ctx) {}
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

    /**
     * Retrieves the item associated with the given block if item generation is enabled.
     *
     * @param block The custom block.
     * @return The {@link Item}, or {@code null} if generation is disabled.
     */
    public static Item asItem(Block block) {
        if (!block.generateItem()) return null;
        return BuiltinRegistries.ITEMS.get(block.id().toString());
    }

    /**
     * Checks whether this block is part of the specified {@link BlockTag}.
     *
     * @param id the {@link ResourceLocation} of the tag to check
     * @return true if this block is included in the tag, false otherwise
     */
    public boolean hasTag(ResourceLocation id) {
        BlockTag tag = BuiltinRegistries.BLOCK_TAGS.get(id.toString());
        return tag.contains(this);
    }

    /**
     * Creates a shallow clone of this block. The cloned block will have the same
     * ID, material, and a cloned {@link BlockData} instance.
     *
     * @return A copy of this {@link Block} instance.
     */
    @Override
    public Block clone() {
        try {
            Block clone = (Block) super.clone();
            clone.data = this.data.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
