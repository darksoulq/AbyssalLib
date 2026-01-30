package com.github.darksoulq.abyssallib.world.block;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BlockBridge;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.tag.impl.BlockTag;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.BlockItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Represents a custom block type in the AbyssalLib framework.
 * <p>
 * This class defines the behavior, properties, and event hooks for custom blocks.
 * To create a custom block, extend this class or use the default implementation
 * with modified {@link BlockProperties}.
 */
public class CustomBlock implements Cloneable {

    /**
     * The unique identifier of this block.
     */
    protected final Identifier id;

    /**
     * The underlying Bukkit material of this block.
     */
    private Material material = Material.DIRT;

    /**
     * The location of this block instance in the world.
     */
    private Location location;

    /**
     * The associated block entity, if any.
     */
    private BlockEntity entity;

    /**
     * The physical and interaction properties of this block.
     */
    public BlockProperties properties = BlockProperties.of().build();

    /**
     * Constructs a new CustomBlock with the given identifier.
     *
     * @param id the unique identifier
     */
    public CustomBlock(Identifier id) {
        this.id = id;
    }

    /**
     * Constructs a new CustomBlock with the given identifier and base material.
     *
     * @param id       the unique identifier
     * @param material the base vanilla material
     */
    public CustomBlock(Identifier id, Material material) {
        this.id = id;
        this.material = material;
    }

    /**
     * Gets the unique identifier of this block.
     *
     * @return the block identifier
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Gets the base material of this block.
     *
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Checks if this block should generate a corresponding item.
     *
     * @return true if an item should be generated
     */
    public boolean generateItem() {
        return true;
    }

    /**
     * Provides the supplier for the item representation of this block.
     *
     * @return the item supplier
     */
    public Supplier<Item> getItem() {
        return () -> {
            Item item = new Item(id, material);
            item.setData(new BlockItem(this.id));
            return item;
        };
    }

    /**
     * Creates a new block entity instance for this block.
     * Override this to provide custom logic.
     *
     * @param loc the location of the block
     * @return a new BlockEntity, or null
     */
    public BlockEntity createBlockEntity(Location loc) {
        return null;
    }

    /**
     * Sets the active block entity for this block instance.
     *
     * @param entity the entity to set
     */
    @ApiStatus.Internal
    public void setEntity(BlockEntity entity) {
        this.entity = entity;
    }

    /**
     * Gets the location of this block.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of this block.
     *
     * @param location the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the active block entity.
     *
     * @return the entity, or null
     */
    public BlockEntity getEntity() {
        return entity;
    }

    /**
     * Places the block at the specified Bukkit block location.
     *
     * @param block   the target block
     * @param loading true if this is being loaded from disk, false if newly placed
     */
    public void place(Block block, boolean loading) {
        if (!material.isBlock()) {
            AbyssalLib.getInstance().getLogger().severe("Invalid block material for " + id);
            return;
        }

        setLocation(block.getLocation());

        if (!loading) {
            if (block.getType() != material) block.setType(material);
            BlockEntity newEntity = createBlockEntity(getLocation());
            if (newEntity != null) {
                setEntity(newEntity);
            }

            BlockManager.register(this);
        } else {
            onLoad();
        }
    }

    /**
     * Gets the custom loot table for this block.
     *
     * @return the loot table, or null
     */
    public LootTable getDrops() {
        return null;
    }

    /**
     * Calculates the experience to drop based on block properties.
     *
     * @param player       the player breaking the block
     * @param fortuneLevel the fortune enchantment level
     * @param silkTouch    whether silk touch is used
     * @return amount of XP to drop
     */
    public int getExpToDrop(Player player, int fortuneLevel, boolean silkTouch) {
        if (silkTouch) return 0;
        if (properties.minExp == 0 && properties.maxExp == 0) return 0;

        return ThreadLocalRandom.current().nextInt(properties.minExp, properties.maxExp + 1);
    }

    /**
     * Checks if this block possesses a specific tag.
     *
     * @param id the tag identifier
     * @return true if tagged
     */
    public boolean hasTag(Identifier id) {
        if (!(Registries.TAGS.get(id.toString()) instanceof BlockTag tag)) {
            AbyssalLib.getInstance().getLogger().severe("Unknown tag: " + id);
            return false;
        }
        BridgeBlock<?> block = BlockBridge.get(Identifier.of("abyssallib", this.id.getNamespace(), this.id.getPath()));
        if (block == null) return false;
        return tag.contains(block);
    }

    /**
     * Retrieves the CustomBlock instance associated with a Bukkit Block.
     *
     * @param block the bukkit block
     * @return the custom block, or null
     */
    public static CustomBlock from(Block block) {
        if (block == null) return null;
        return BlockManager.get(block.getLocation());
    }

    /**
     * Retrieves the CustomBlock instance at a specific location.
     *
     * @param loc the location
     * @return the custom block, or null
     */
    public static CustomBlock from(Location loc) {
        if (loc == null) return null;
        return BlockManager.get(loc);
    }

    /**
     * Gets the Item associated with a CustomBlock type.
     *
     * @param block the custom block
     * @return the item, or null
     */
    public static Item asItem(CustomBlock block) {
        if (!block.generateItem()) return null;
        return Registries.ITEMS.get(block.getId().toString());
    }

    /**
     * Compares this block to another object.
     *
     * @param o the other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomBlock block)) return false;
        return Objects.equals(id, block.id) && Objects.equals(location, block.location);
    }

    /**
     * Generates a hash code for this block.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Clones this custom block instance.
     *
     * @return a clone of the block
     */
    @Override
    public CustomBlock clone() {
        try {
            return (CustomBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the block is loaded from disk.
     */
    public void onLoad() {}

    /**
     * Called when the block is unloaded (chunk unload or server stop).
     */
    public void onUnLoad() {}

    /**
     * Called when a player interacts with the block.
     *
     * @param event the interaction event
     * @return ActionResult to control the outcome
     */
    public ActionResult onInteract(BlockInteractionEvent event) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is placed by a player.
     *
     * @param player the player
     * @param loc    the location
     * @param stack  the item stack used
     * @return ActionResult
     */
    public ActionResult onPlaced(Player player, Location loc, ItemStack stack) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is broken by a player.
     *
     * @param player the player
     * @param loc    the location
     * @param tool   the tool used
     * @return ActionResult
     */
    public ActionResult onBreak(Player player, Location loc, ItemStack tool) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is destroyed by an explosion.
     *
     * @param eCause     the entity that caused the explosion, if any
     * @param blockCause the block that caused the explosion, if any
     * @return ActionResult
     */
    public ActionResult onDestroyedByExplosion(@Nullable Entity eCause, @Nullable Block blockCause) {
        return ActionResult.PASS;
    }

    /**
     * Called when an entity lands on the block (fall damage calculation).
     *
     * @param entity the entity
     */
    public void onLanded(Entity entity) {}

    /**
     * Called when an entity steps onto the block.
     *
     * @param entity the entity
     */
    public void onSteppedOn(LivingEntity entity) {}

    /**
     * Called when the block receives a redstone update.
     *
     * @param oldCurrent previous signal strength
     * @param newCurrent new signal strength
     * @return the adjusted signal strength
     */
    public int onRedstone(int oldCurrent, int newCurrent) {
        return oldCurrent;
    }

    /**
     * Called when a projectile hits the block.
     *
     * @param projectile the projectile
     * @return ActionResult
     */
    public ActionResult onProjectileHit(Projectile projectile) {
        return ActionResult.PASS;
    }

    /**
     * Called when a neighbor block changes or a physics update occurs.
     *
     * @param source the block causing the update
     * @return ActionResult
     */
    public ActionResult onNeighborUpdate(Block source) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is moved by a piston.
     *
     * @param direction the direction of movement
     * @return ActionResult
     */
    public ActionResult onPistonMove(BlockFace direction) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block is fertilized with Bone Meal.
     *
     * @param player the player using bone meal
     * @return ActionResult
     */
    public ActionResult onBoneMeal(Player player) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block fades (e.g. ice melting).
     *
     * @param block    the block instance
     * @param newState the state being transitioned to
     * @return ActionResult
     */
    public ActionResult onFade(Block block, BlockState newState) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block forms (e.g. snow forming).
     *
     * @param block    the block instance
     * @param newState the state being transitioned to
     * @return ActionResult
     */
    public ActionResult onForm(Block block, BlockState newState) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block grows (e.g. crops).
     *
     * @param block    the block instance
     * @param newState the state being transitioned to
     * @return ActionResult
     */
    public ActionResult onGrow(Block block, BlockState newState) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block ignites.
     *
     * @param cause           the reason for ignition
     * @param ignitingEntity  the entity causing ignition
     * @param ignitingBlock   the block causing ignition
     * @return ActionResult
     */
    public ActionResult onIgnite(BlockIgniteEvent.IgniteCause cause, Entity ignitingEntity, Block ignitingBlock) {
        return ActionResult.PASS;
    }

    /**
     * Called when the block spreads (e.g. fire, mushrooms).
     *
     * @param block    the block instance
     * @param source   the source of the spread
     * @param newState the state being transitioned to
     * @return ActionResult
     */
    public ActionResult onSpread(Block block, Block source, BlockState newState) {
        return ActionResult.PASS;
    }

    /**
     * Called when leaves decay.
     *
     * @return ActionResult
     */
    public ActionResult onLeavesDecay() {
        return ActionResult.PASS;
    }

    /**
     * Called when a sponge absorbs water.
     *
     * @param spongedBlocks the list of affected block states
     * @return ActionResult
     */
    public ActionResult onSpongeAbsorb(List<BlockState> spongedBlocks) {
        return ActionResult.PASS;
    }

    /**
     * Called when a sign is changed.
     *
     * @param player the player changing the sign
     * @param side   the side of the sign affected
     * @return ActionResult
     */
    public ActionResult onSignChange(Player player, Side side) {
        return ActionResult.PASS;
    }
}