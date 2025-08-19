package com.github.darksoulq.abyssallib.world.level.block;

import java.util.Set;

/**
 * Defines the physical and interaction behavior of a custom block.
 * This includes how hard the block is to break, whether it supports enchantments like Silk Touch or Fortune,
 * and which tools are valid for breaking or dropping items.
 */
public class BlockProperties {

    /**
     * Hardness of the block, used to determine break speed.
     * Higher values make the block slower to break.
     */
    public final float hardness;

    /**
     * Whether the block supports Silk Touch to drop itself.
     * If true and the player has Silk Touch, the block may drop itself instead of standard drops.
     */
    public final boolean requireSilkTouch;

    /**
     * Whether block supports fortune enchantment
     */
    public final boolean allowFortune;

    /**
     * Private constructor used by {@link Builder}.
     *
     * @param hardness the block's hardness
     * @param requireSilkTouch whether Silk Touch is allowed
     * @param allowFortune whether Fortune is allowed
     */
    private BlockProperties(
            float hardness,
            boolean requireSilkTouch,
            boolean allowFortune) {
        this.hardness = hardness;
        this.requireSilkTouch = requireSilkTouch;
        this.allowFortune = allowFortune;
    }

    /**
     * Creates a new {@link Builder} for defining custom block properties.
     *
     * @return a new builder instance
     */
    public static Builder of() {
        return new Builder();
    }

    /**
     * Builder for constructing {@link BlockProperties} instances.
     */
    public static class Builder {
        private float hardness = 1.0f;
        private boolean requireSilkTouch = false;
        private boolean allowFortune = false;

        /**
         * Sets the hardness of the block.
         *
         * @param value the block's hardness
         * @return this builder
         */
        public Builder hardness(float value) {
            this.hardness = value;
            return this;
        }

        /**
         * Enables or disables Silk Touch support.
         *
         * @param value true if Silk Touch should allow dropping the block itself
         * @return this builder
         */
        public Builder requireSilkTouch(boolean value) {
            this.requireSilkTouch = value;
            return this;
        }

        /**
         * Enables or disables Fortune support.
         *
         * @param value true if Fortune should affect drops
         * @return this builder
         */
        public Builder allowFortune(boolean value) {
            this.allowFortune = value;
            return this;
        }

        /**
         * Builds a new {@link BlockProperties} instance with the configured values.
         *
         * @return a new block properties instance
         */
        public BlockProperties build() {
            return new BlockProperties(
                    hardness,
                    requireSilkTouch,
                    allowFortune
            );
        }
    }
}
