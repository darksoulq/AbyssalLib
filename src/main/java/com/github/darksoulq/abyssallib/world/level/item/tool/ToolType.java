package com.github.darksoulq.abyssallib.world.level.item.tool;

import org.bukkit.Material;

/**
 * Represents a general classification of tools (e.g., PICKAXE, SHOVEL, etc.).
 */
public enum ToolType {
    SWORD,
    PICKAXE,
    SHOVEL,
    AXE,
    HOE,
    SHEARS,
    BOW,
    CROSSBOW,
    OTHER;

    /** Maps common vanilla materials to tool types. */
    public static ToolType fromMaterial(Material material) {
        String name = material.name();
        if (name.endsWith("_SWORD")) return SWORD;
        if (name.endsWith("_PICKAXE")) return PICKAXE;
        if (name.endsWith("_SHOVEL")) return SHOVEL;
        if (name.endsWith("_AXE")) return AXE;
        if (name.endsWith("_HOE")) return HOE;
        if (name.contains("SHEARS")) return SHEARS;
        if (name.contains("BOW")) return name.contains("CROSS") ? CROSSBOW : BOW;
        return OTHER;
    }
}
