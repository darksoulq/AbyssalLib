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

public abstract class Multiblock implements Cloneable {
    protected final Identifier id;
    protected int rotation = 0;
    protected boolean mirror = false;
    protected Location origin;
    protected MultiblockEntity entity;
    protected Map<RelativeBlockPos, MultiblockChoice> pattern = new LinkedHashMap<>();

    public Multiblock(Identifier id) {
        this.id = id;
    }

    public void onLoad() {}
    public void onUnLoad() {}
    public ActionResult onConstruct(Player player, Multiblock mb, ItemStack held) {
        return ActionResult.PASS;
    }
    public ActionResult onBreak(Player player, Multiblock mb, ItemStack tool) {
        return ActionResult.PASS;
    }
    public ActionResult onDestroyedByExplosion(@Nullable Entity eCause, @Nullable Block bCause) {
        return ActionResult.PASS;
    }
    public int onRedstone(int old, int now) {
        return 0;
    }
    public ActionResult onProjectileHit(Projectile projectile) {
        return ActionResult.PASS;
    }

    public abstract MultiblockChoice getTriggerChoice();
    public MultiblockEntity createMultiblockEntity(Location origin) {
        return null;
    }
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

    public int getRotation() {
        return rotation;
    }
    /* DO NOT USE THIS METHOD */
    @ApiStatus.Internal
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    public boolean isMirrored() {
        return mirror;
    }
    /* DO NOT USE THIS METHOD */
    @ApiStatus.Internal
    public void setMirrored(boolean mirror) {
        this.mirror = mirror;
    }
    public Location getOrigin() { return origin; }
    public Identifier getId() {
        return id;
    }
    public MultiblockEntity getEntity() { return entity; }
    public void setEntity(MultiblockEntity entity) { this.entity = entity; }

    public boolean isPartOfMultiblock(Location loc) {
        if (origin == null) return false;
        Multiblock found = MultiblockManager.getAt(loc);
        return found == this;
    }
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
    public Map<RelativeBlockPos, MultiblockChoice> getPattern() { return pattern; }

    public static Location absolute(Location origin, RelativeBlockPos rel) {
        return origin.clone().add(rel.x(), rel.y(), rel.z());
    }
    public static Multiblock from(Location location) {
        return MultiblockManager.getAt(location);
    }
    public static Multiblock from(Block block) {
        return MultiblockManager.getAt(block.getLocation());
    }
    public static boolean isPartOf(Location location) {
        return MultiblockManager.isPartOfMultiblock(location);
    }
    public static boolean isPartOf(Block block) {
        return MultiblockManager.isPartOfMultiblock(block.getLocation());
    }
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
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Multiblock multiblock)) return false;
        return Objects.equals(id, multiblock.id) && Objects.equals(origin, multiblock.origin);
    }
}
