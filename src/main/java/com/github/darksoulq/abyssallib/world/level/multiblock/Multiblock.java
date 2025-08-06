package com.github.darksoulq.abyssallib.world.level.multiblock;

import com.github.darksoulq.abyssallib.util.TextUtil;
import com.github.darksoulq.abyssallib.world.level.block.internal.BlockManager;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Multiblock {
    private final Identifier id;
    private final Class<? extends MultiblockData> dataClass;
    private final Map<RelativeBlockPos, MultiblockChoice> pattern = new HashMap<>();

    private int[] min = new int[3];
    private int[] max = new int[3];

    public Multiblock(Identifier id, Class<? extends MultiblockData> dataClass) {
        this.id = id;
        this.dataClass = dataClass;
        Arrays.fill(min, 0);
        Arrays.fill(max, 0);
    }

    public Multiblock(Identifier id) {
        this.id = id;
        this.dataClass = EmptyData.class;
        Arrays.fill(min, 0);
        Arrays.fill(max, 0);
    }

    protected void structure(int x, int y, int z, MultiblockChoice choice) {
        pattern.put(new RelativeBlockPos(x, y, z), choice);
        updateBounds(x, y, z);
    }

    private void updateBounds(int x, int y, int z) {
        if (x < min[0]) min[0] = x;
        if (y < min[1]) min[1] = y;
        if (z < min[2]) min[2] = z;
        if (x > max[0]) max[0] = x;
        if (y > max[1]) max[1] = y;
        if (z > max[2]) max[2] = z;
    }

    public Identifier getId() {
        return id;
    }

    public boolean matchesAt(Block origin) {
        for (var e : pattern.entrySet()) {
            Block b = origin.getRelative(e.getKey().x(), e.getKey().y(), e.getKey().z());
            if (!e.getValue().matches(b)) return false;
        }
        return true;
    }

    public Block findOriginFrom(Block clicked) {
        int minX = min[0], minY = min[1], minZ = min[2];
        int maxX = max[0], maxY = max[1], maxZ = max[2];

        for (int dy = minY; dy <= maxY; dy++) {
            for (int dx = minX; dx <= maxX; dx++) {
                for (int dz = minZ; dz <= maxZ; dz++) {
                    Block cand = clicked.getRelative(dx, dy, dz);
                    if (matchesAt(cand)) return cand;
                }
            }
        }
        return null;
    }

    public MultiblockData createData() {
        try {
            return dataClass != null ? dataClass.getDeclaredConstructor().newInstance() : null;
        } catch (Exception e) {
            throw new RuntimeException("Could not create MultiblockData", e);
        }
    }

    public MultiblockData deserializeData(String json) {
        return dataClass != null ? TextUtil.GSON.fromJson(json, dataClass) : null;
    }

    public Map<RelativeBlockPos, MultiblockChoice> getPattern() {
        return pattern;
    }

    public boolean isTrigger(Block clicked) { return true; }

    public void onActivate(Player player, MultiblockInstance instance) {}

    public void onInteract(Player player, MultiblockInstance inst, Block clicked) {}

    public void onBreak(Player breaker, MultiblockInstance inst, Block brokenPart) {}

    public static class EmptyData extends MultiblockData {}
}
