package com.github.darksoulq.abyssallib.world.multiblock;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.world.multiblock.internal.MultiblockManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The base class for all multiblock structures in the framework.
 * <p>
 * Multiblocks are complex arrangements of blocks that behave as a single logic unit.
 * This class handles pattern matching across four rotations and optional mirroring,
 * coordinate transformations, and life-cycle events.
 */
public abstract class Multiblock implements Cloneable {

    /** The unique identifier for this multiblock type. */
    protected final Identifier id;

    /** The current rotation (0-3) assigned during matching. */
    protected int rotation = 0;

    /** Whether the structure is currently mirrored. */
    protected boolean mirror = false;

    /** The world location used as the trigger/pivot for the multiblock. */
    protected Location origin;

    /** The logical entity associated with this specific multiblock instance. */
    protected MultiblockEntity entity;

    /** The defined pattern of relative positions and their required block choices. */
    protected Map<RelativeBlockPos, MultiblockChoice> pattern = new LinkedHashMap<>();

    /**
     * Constructs a new Multiblock with a specific identifier.
     *
     * @param id The unique identifier.
     */
    public Multiblock(Identifier id) {
        this.id = id;
    }

    /**
     * Called when the multiblock instance is loaded into the world.
     */
    public void onLoad() {}

    /**
     * Called when the multiblock instance is unloaded from the world.
     */
    public void onUnLoad() {}

    /**
     * Called when the multiblock is successfully constructed in the world.
     *
     * @param player The player who completed the structure.
     * @param mb     The multiblock instance.
     * @param held   The item held during construction.
     * @return The result of the action.
     */
    public ActionResult onConstruct(Player player, Multiblock mb, ItemStack held) {
        return ActionResult.PASS;
    }

    /**
     * Called when the multiblock is broken.
     *
     * @param player The player who broke a component.
     * @param mb     The multiblock instance.
     * @param tool   The tool used.
     * @return The result of the action.
     */
    public ActionResult onBreak(Player player, Multiblock mb, ItemStack tool) {
        return ActionResult.PASS;
    }

    /**
     * Called when the structure is destroyed by an explosion.
     *
     * @param eCause The entity cause.
     * @param bCause The block cause.
     * @return The result of the action.
     */
    public ActionResult onDestroyedByExplosion(@Nullable Entity eCause, @Nullable Block bCause) {
        return ActionResult.PASS;
    }

    /**
     * Called when a redstone update affects the structure.
     *
     * @param old The previous signal strength.
     * @param now The current signal strength.
     * @return The resulting signal strength.
     */
    public int onRedstone(int old, int now) {
        return 0;
    }

    /**
     * Called when a projectile strikes any part of the structure.
     *
     * @param projectile The projectile involved.
     * @return The result of the action.
     */
    public ActionResult onProjectileHit(Projectile projectile) {
        return ActionResult.PASS;
    }

    /**
     * @return The block choice required at the trigger location to start construction.
     */
    public abstract MultiblockChoice getTriggerChoice();

    /**
     * Creates a new multiblock entity for this instance.
     *
     * @param origin The origin location.
     * @return A new {@link MultiblockEntity}, or null.
     */
    public MultiblockEntity createMultiblockEntity(Location origin) {
        return null;
    }

    /**
     * Places the multiblock at the specified location and registers it.
     *
     * @param trigger      The location where the trigger block was placed.
     * @param fromDatabase Whether this is being loaded from persistent storage.
     */
    public void place(Location trigger, boolean fromDatabase) {
        this.origin = trigger;
        Map<Location, org.bukkit.block.BlockState> snapshot = new HashMap<>();
        try {
            for (Map.Entry<RelativeBlockPos, MultiblockChoice> e : pattern.entrySet()) {
                RelativeBlockPos rotated = transform(e.getKey(), rotation, mirror);
                Location abs = absolute(trigger, rotated);
                org.bukkit.block.Block b = abs.getBlock();
                snapshot.put(abs, b.getState());
            }
            if (!fromDatabase) {
                MultiblockEntity ent = createMultiblockEntity(trigger);
                if (ent != null) {
                    setEntity(ent);
                    ent.onLoad();
                }
                MultiblockManager.register(this);
            }

        } catch (Throwable t) {
            for (org.bukkit.block.BlockState saved : snapshot.values()) {
                try {
                    saved.update(true);
                } catch (Throwable ignore) {}
            }
            throw new RuntimeException(t);
        }
    }

    /** @return The current rotation index (0-3). */
    public int getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation index. Internal use only.
     *
     * @param rotation The new rotation.
     */
    @ApiStatus.Internal
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    /** @return {@code true} if the multiblock is mirrored. */
    public boolean isMirrored() {
        return mirror;
    }

    /**
     * Sets the mirror state. Internal use only.
     *
     * @param mirror The mirror state.
     */
    @ApiStatus.Internal
    public void setMirrored(boolean mirror) {
        this.mirror = mirror;
    }

    /** @return The origin location of the multiblock. */
    public Location getOrigin() { return origin; }

    /** @return The unique identifier of this multiblock. */
    public Identifier getId() {
        return id;
    }

    /** @return The associated entity, if any. */
    public MultiblockEntity getEntity() { return entity; }

    /** @param entity The entity to associate. */
    public void setEntity(MultiblockEntity entity) { this.entity = entity; }

    /**
     * Checks if a location is part of this specific multiblock instance.
     *
     * @param loc The location to check.
     * @return {@code true} if the location belongs to this multiblock.
     */
    public boolean isPartOfMultiblock(Location loc) {
        if (origin == null) return false;
        Multiblock found = MultiblockManager.getAt(loc);
        return found == this;
    }

    /**
     * Attempts to find a valid rotation/mirroring state that matches the world's blocks.
     *
     * @param trigger The location where matching starts.
     * @return {@code true} if a match was found; {@code false} otherwise.
     */
    public boolean matchesLayout(Location trigger) {
        for (boolean mirror : new boolean[]{false, true}) {
            for (int rot = 0; rot < 4; rot++) {
                boolean ok = true;
                for (Map.Entry<RelativeBlockPos, MultiblockChoice> e : pattern.entrySet()) {
                    RelativeBlockPos transformed = transform(e.getKey(), rot, mirror);
                    Location abs = absolute(trigger, transformed);
                    Block block = abs.getBlock();

                    if (MultiblockManager.isPartOfMultiblock(abs)) {
                        ok = false;
                        break;
                    }
                    if (!e.getValue().matches(block)) {
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    this.rotation = rot;
                    this.mirror = mirror;
                    return true;
                }
            }
        }
        return false;
    }

    /** @return The pattern defining this multiblock. */
    public Map<RelativeBlockPos, MultiblockChoice> getPattern() { return pattern; }

    /**
     * Converts a relative position to an absolute location.
     *
     * @param origin The origin reference.
     * @param rel    The relative coordinates.
     * @return The absolute {@link Location}.
     */
    public static Location absolute(Location origin, RelativeBlockPos rel) {
        return origin.clone().add(rel.x(), rel.y(), rel.z());
    }

    /**
     * Retrieves the multiblock instance at a location.
     *
     * @param location The location to query.
     * @return The {@link Multiblock} found, or null.
     */
    public static Multiblock from(Location location) {
        return MultiblockManager.getAt(location);
    }

    /**
     * Retrieves the multiblock instance at a block's location.
     *
     * @param block The block to query.
     * @return The {@link Multiblock} found, or null.
     */
    public static Multiblock from(Block block) {
        return MultiblockManager.getAt(block.getLocation());
    }

    /**
     * Checks if a location is part of any registered multiblock.
     *
     * @param location The location to check.
     * @return {@code true} if it is part of a multiblock.
     */
    public static boolean isPartOf(Location location) {
        return MultiblockManager.isPartOfMultiblock(location);
    }

    /**
     * Checks if a block is part of any registered multiblock.
     *
     * @param block The block to check.
     * @return {@code true} if it is part of a multiblock.
     */
    public static boolean isPartOf(Block block) {
        return MultiblockManager.isPartOfMultiblock(block.getLocation());
    }

    /**
     * Transforms a relative position based on rotation and mirroring.
     *
     * @param p      The base relative position.
     * @param rot    The rotation index (0-3).
     * @param mirror Whether to mirror on the X axis.
     * @return The transformed {@link RelativeBlockPos}.
     */
    public static RelativeBlockPos transform(RelativeBlockPos p, int rot, boolean mirror) {
        int x = p.x();
        int y = p.y();
        int z = p.z();

        if (mirror) {
            x = -x;
        }

        return switch (rot & 3) {
            case 0 -> new RelativeBlockPos(x, y, z);
            case 1 -> new RelativeBlockPos(-z, y, x);
            case 2 -> new RelativeBlockPos(-x, y, -z);
            case 3 -> new RelativeBlockPos(z, y, -x);
            default -> throw new IllegalStateException();
        };
    }

    /**
     * Clones the multiblock instance.
     *
     * @return A deep copy of the multiblock.
     */
    @Override
    public Multiblock clone() {
        try {
            Multiblock copy = (Multiblock) super.clone();
            copy.pattern = new LinkedHashMap<>();
            copy.pattern.putAll(this.pattern);
            copy.mirror = mirror;
            copy.rotation = rotation;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return The hash code. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Compares multiblock instances.
     *
     * @param o The other object.
     * @return {@code true} if the ID and origin match.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Multiblock multiblock)) return false;
        return Objects.equals(id, multiblock.id) && Objects.equals(origin, multiblock.origin);
    }
}