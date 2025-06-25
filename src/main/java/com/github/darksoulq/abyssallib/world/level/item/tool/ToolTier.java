package com.github.darksoulq.abyssallib.world.level.item.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a tool tier with a floating-point effectiveness level
 * used for determining if a tool is strong enough to break a block.
 *
 * @param id            Unique ID of the tier (e.g., "iron")
 * @param effectiveness Tool effectiveness level (used to compare with required tier)
 */
public record ToolTier(String id, float effectiveness) {

    /**
     * Internal registry for all tiers by ID
     */
    private static final Map<String, ToolTier> REGISTRY = new HashMap<>();

    /**
     * Wooden tool tier (e.g. Wooden Pickaxe)
     */
    public static final ToolTier WOOD = new ToolTier("wood", 1.0f);

    /**
     * Stone tool tier
     */
    public static final ToolTier STONE = new ToolTier("stone", 2.0f);

    /**
     * Golden tool tier
     */
    public static final ToolTier GOLD = new ToolTier("gold", 3.0f);

    /**
     * Iron tool tier
     */
    public static final ToolTier IRON = new ToolTier("iron", 4.0f);

    /**
     * Diamond tool tier
     */
    public static final ToolTier DIAMOND = new ToolTier("diamond", 5.0f);

    /**
     * Netherite tool tier
     */
    public static final ToolTier NETHERITE = new ToolTier("netherite", 6.0f);

    /**
     * Constructs a new ToolTier and registers it automatically.
     *
     * @param id            unique identifier (e.g. "gold")
     * @param effectiveness effectiveness level (higher = better)
     */
    public ToolTier(String id, float effectiveness) {
        this.id = id;
        this.effectiveness = effectiveness;
        REGISTRY.put(id, this);
    }

    /**
     * Returns whether this tier is at least as strong as the given one.
     *
     * @param other the other tier to compare against
     * @return true if this tier's effectiveness is greater than or equal to the other's
     */
    public boolean isAtLeast(ToolTier other) {
        return this.effectiveness >= other.effectiveness;
    }

    /**
     * Retrieves a registered ToolTier by its ID.
     *
     * @param id the ID to look up
     * @return the ToolTier instance or null if not found
     */
    public static ToolTier get(String id) {
        return REGISTRY.get(id);
    }

    /**
     * Returns an unmodifiable map of all registered tool tiers.
     */
    public static Map<String, ToolTier> all() {
        return Map.copyOf(REGISTRY);
    }
}
