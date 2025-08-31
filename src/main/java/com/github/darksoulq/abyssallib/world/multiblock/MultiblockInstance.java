package com.github.darksoulq.abyssallib.world.multiblock;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class MultiblockInstance {
    private final Location origin;
    private final Multiblock type;
    private MultiblockData data;

    public MultiblockInstance(Location origin, Multiblock type) {
        this.origin = origin;
        this.type = type;
        this.data = type.createData();
    }

    public MultiblockChoice getChoiceAtBlock(Block block) {
        if (!block.getWorld().equals(origin.getWorld())) return null;

        int dx = block.getX() - origin.getBlockX();
        int dy = block.getY() - origin.getBlockY();
        int dz = block.getZ() - origin.getBlockZ();

        return type.getPattern().get(new RelativeBlockPos(dx, dy, dz));
    }
    public Location getOrigin() { return origin; }
    public Multiblock getType() { return type; }
    public MultiblockData getData() {
        return data;
    }
    public void setData(MultiblockData data) {
        this.data = data;
    }

    public void tickIfApplicable() {
        if (type instanceof Tickable t) {
            t.tick(this);
        }
    }
}
