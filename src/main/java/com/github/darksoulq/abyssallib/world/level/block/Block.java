package com.github.darksoulq.abyssallib.world.level.block;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.level.data.tag.BlockTag;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a custom block type in the AbyssalLib framework.
 * <p>
 * This class defines basic properties and behaviors for blocks, including material type,
 * unique identifier, entity association, and event hooks.
 */
public class Block implements Cloneable {

    /**
     * The unique identifier of this block.
     */
    protected final Identifier id;

    /**
     * The Bukkit material representing this block.
     */
    private Material material = Material.DIRT;

    /**
     * The Bukkit location of this block instance.
     */
    private Location location;

    /**
     * The block entity associated with this block, if any.
     */
    private BlockEntity entity;

    /**
     * Whether to allow physics for the block (only applies to blocks with physics like sand).
     */
    public boolean allowPhysics = false;

    /**
     * The properties associated with this block.
     */
    public BlockProperties properties = BlockProperties.of().build();

    /**
     * Constructs a new {@code Block} with the given identifier.
     *
     * @param id the unique identifier for this block
     */
    public Block(Identifier id) {
        this.id = id;
    }

    /**
     * Constructs a new {@code Block} with the given identifier and block material.
     *
     * @param id the unique identifier for this block
     */
    public Block(Identifier id, Material material) {
        this.id = id;
        this.material = material;
    }

    /**
     * Returns the unique identifier of this block.
     *
     * @return the {@link Identifier} of the block
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Returns the Bukkit material of this block.
     *
     * @return the {@link Material} of the block
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Returns whether this block generates an item representation.
     *
     * @return {@code true} if this block generates an item, otherwise {@code false}
     */
    public boolean generateItem() {
        return true;
    }

    /**
     * Returns a {@link Supplier} that creates a new {@link Item} representing this block.
     *
     * @return a supplier for the item representation of this block
     */
    public Supplier<Item> getItem() {
        return () -> {
            Item item = new Item(id, material);
            item.getSettings().blockItem(this);
            return item;
        };
    }

    /**
     * Creates and returns a new {@link BlockEntity} for this block at the specified location.
     *
     * @param loc the Bukkit location where the block entity should be created
     * @return a new {@link BlockEntity} instance, or {@code null} if none
     */
    public BlockEntity createBlockEntity(Location loc) {
        return null;
    }

    /**
     * Sets the {@link BlockEntity} associated with this block.
     *
     * @param entity the block entity to associate
     */
    @ApiStatus.Internal
    public void setEntity(BlockEntity entity) {
        this.entity = entity;
    }

    /**
     * Returns the Bukkit location of this block.
     *
     * @return the location of the block
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the Bukkit location of this block.
     *
     * @param location the new location of the block
     */
    protected void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns the {@link BlockEntity} associated with this block, if any.
     *
     * @return the associated block entity, or {@code null} if none
     */
    public BlockEntity getEntity() {
        return entity;
    }

    /**
     * Places this block into the world at the location specified by {@code bukkitBlock}.
     *
     * @param bukkitBlock the Bukkit block
     */
    public void place(org.bukkit.block.Block bukkitBlock, boolean loading) {
        if (!material.isBlock()) {
            AbyssalLib.getInstance().getLogger().severe("Invalid block material for " + id);
            return;
        }

        setLocation(bukkitBlock.getLocation());

        if (!loading) {
            bukkitBlock.setType(material);
            BlockEntity newEntity = createBlockEntity(getLocation());
            if (newEntity != null) {
                setEntity(newEntity);
            }

            BlockManager.register(this);
        }
    }

    /**
     * Returns the loot table associated with this block, or {@code null} if none.
     *
     * @return the {@link LootTable} for this block or {@code null}
     */
    public LootTable getLootTable() {
        return null;
    }

    /**
     * Returns the amount of experience (XP) this block gives when mined.
     *
     * @return the XP amount, or 0 if none
     */
    public int exp() {
        return 0;
    }

    /**
     * Checks whether this block has the specified block tag.
     *
     * @param id the identifier of the block tag to check
     * @return {@code true} if this block has the tag, otherwise {@code false}
     */
    public boolean hasTag(Identifier id) {
        BlockTag tag = BuiltinRegistries.BLOCK_TAGS.get(id.toString());
        return tag != null && tag.contains(this);
    }

    /**
     * Checks whether the given Bukkit block is a custom block managed by AbyssalLib,
     * and returns the corresponding {@link Block} instance if it is.
     *
     * @param bukkitBlock the Bukkit block to check
     * @return the associated custom {@link Block}, or {@code null} if not a custom block
     */
    public static Block from(org.bukkit.block.Block bukkitBlock) {
        if (bukkitBlock == null) return null;
        return BlockManager.get(bukkitBlock.getLocation());
    }

    /**
     * Returns the item representation of the given custom block if item generation is enabled.
     *
     * @param block the custom block
     * @return the corresponding {@link Item}, or {@code null} if item generation is disabled
     */
    public static Item asItem(Block block) {
        if (!block.generateItem()) return null;
        return BuiltinRegistries.ITEMS.get(block.getId().toString());
    }

    /**
     * Indicates whether this block is equal to another object.
     *
     * @param o the object to compare
     * @return {@code true} if equal, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Block block)) return false;
        return Objects.equals(id, block.id);
    }

    /**
     * Returns the hash code of this block.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Clones this block
     *
     * @return The cloned block
     */
    @Override
    public Block clone() {
        try {
            Block cloned = (Block) super.clone();
            cloned.setLocation(this.getLocation().clone());
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the block is placed.
     *
     * @param player the player placing the block
     * @param loc the location at which the block is being placed
     * @param stack the {@link ItemStack} used to place the block
     * @return {@link ActionResult} to cancel vanilla event or allow it
     */
    public ActionResult onPlaced(Player player, Location loc, ItemStack stack) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is broken.
     *
     * @param player the player who broke the block
     * @param loc the location at which the block was broken
     * @param tool the {@link ItemStack} used to break the block
     * @return {@link ActionResult} to cancel vanilla event or allow it
     */
    public ActionResult onBreak(Player player, Location loc, ItemStack tool) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is destroyed by an explosion.
     *
     * @param eCause the source entity of the explosion, or {@code null}
     * @param blockCause the source block of the explosion, or {@code null}
     * @return {@link ActionResult} to cancel vanilla event or allow it
     */
    public ActionResult onDestroyedByExplosion(@Nullable Entity eCause, @Nullable org.bukkit.block.Block blockCause) {
        return ActionResult.PASS;
    }

    /**
     * Called when an entity lands on this block.
     *
     * @param entity the entity landing on the block
     */
    public void onLanded(Entity entity) {}

    /**
     * Called when an entity steps on this block.
     *
     * @param entity the entity stepping on the block
     */
    public void onSteppedOn(LivingEntity entity) {}

    /**
     * Called when this block is hit by a projectile.
     *
     * @param shooter the entity that shot the projectile
     */
    public void onProjectileHit(Entity shooter) {}
}
