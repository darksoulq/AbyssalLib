package com.github.darksoulq.abyssallib.world.data.loot;

/**
 * An enumeration defining how a custom loot table should interact with existing
 * vanilla loot tables or other overlapping definitions.
 */
public enum MergeStrategy {
    /** Do not interact with other loot tables; this table stands alone. */
    NONE,
    /** Completely replace the contents of the target vanilla loot table with this one. */
    REPLACE,
    /** Append the pools of this table to the target vanilla loot table's existing pools. */
    MERGE
}