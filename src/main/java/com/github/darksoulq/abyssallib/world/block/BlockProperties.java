package com.github.darksoulq.abyssallib.world.block;

import org.jetbrains.annotations.NotNull;

/**
 * Defines the physical and interaction behavior of a custom block.
 * <p>
 * This class uses a builder pattern to configure immutable properties such as hardness,
 * resistance, tool requirements, drops, and reactions to world events.
 */
public class BlockProperties {

    /**
     * Enum representing how a block reacts to piston movement.
     */
    public enum PistonReaction {
        /**
         * The block can be pushed or pulled by a piston.
         */
        MOVE,
        /**
         * The block breaks when pushed or pulled.
         */
        DESTROY,
        /**
         * The block cannot be moved and stops the piston.
         */
        BLOCK,
        /**
         * The block is ignored by sticky pistons (cannot be pulled) but can be pushed.
         */
        PUSH_ONLY
    }

    /**
     * Hardness of the block, used to determine break speed.
     */
    public final float hardness;

    /**
     * Resistance of the block to explosions.
     */
    public final float resistance;

    /**
     * Whether the block supports Silk Touch to drop itself.
     */
    public final boolean requireSilkTouch;

    /**
     * Whether the block supports the Fortune enchantment.
     */
    public final boolean allowFortune;

    /**
     * Whether the block is flammable and can be destroyed by fire.
     */
    public final boolean isFlammable;

    /**
     * Whether the block allows vanilla physics updates.
     */
    public final boolean allowPhysics;

    /**
     * Minimum experience dropped when broken.
     */
    public final int minExp;

    /**
     * Maximum experience dropped when broken.
     */
    public final int maxExp;

    /**
     * How the block reacts to pistons.
     */
    public final PistonReaction pistonReaction;

    private BlockProperties(
        float hardness,
        float resistance,
        boolean requireSilkTouch,
        boolean allowFortune,
        boolean isFlammable,
        boolean allowPhysics,
        int minExp,
        int maxExp,
        PistonReaction pistonReaction) {
        this.hardness = hardness;
        this.resistance = resistance;
        this.requireSilkTouch = requireSilkTouch;
        this.allowFortune = allowFortune;
        this.isFlammable = isFlammable;
        this.allowPhysics = allowPhysics;
        this.minExp = minExp;
        this.maxExp = maxExp;
        this.pistonReaction = pistonReaction;
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
        private float resistance = 1.0f;
        private boolean requireSilkTouch = false;
        private boolean allowFortune = false;
        private boolean isFlammable = false;
        private boolean allowPhysics = false;
        private int minExp = 0;
        private int maxExp = 0;
        private PistonReaction pistonReaction = PistonReaction.MOVE;

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
         * Sets the explosion resistance of the block.
         *
         * @param value the resistance value
         * @return this builder
         */
        public Builder resistance(float value) {
            this.resistance = value;
            return this;
        }

        /**
         * Enables or disables Silk Touch support.
         *
         * @param value true if Silk Touch is required
         * @return this builder
         */
        public Builder requireSilkTouch(boolean value) {
            this.requireSilkTouch = value;
            return this;
        }

        /**
         * Enables or disables Fortune support.
         *
         * @param value true if Fortune affects drops
         * @return this builder
         */
        public Builder allowFortune(boolean value) {
            this.allowFortune = value;
            return this;
        }

        /**
         * Sets whether the block is flammable.
         *
         * @param value true if flammable
         * @return this builder
         */
        public Builder flammable(boolean value) {
            this.isFlammable = value;
            return this;
        }

        /**
         * Sets whether the block accepts vanilla physics updates.
         *
         * @param value true to allow physics
         * @return this builder
         */
        public Builder allowPhysics(boolean value) {
            this.allowPhysics = value;
            return this;
        }

        /**
         * Sets the experience drop range for this block.
         *
         * @param min minimum XP
         * @param max maximum XP
         * @return this builder
         */
        public Builder expDrop(int min, int max) {
            this.minExp = min;
            this.maxExp = max;
            return this;
        }

        /**
         * Sets the piston reaction for this block.
         *
         * @param reaction the {@link PistonReaction}
         * @return this builder
         */
        public Builder pistonReaction(@NotNull PistonReaction reaction) {
            this.pistonReaction = reaction;
            return this;
        }

        /**
         * Builds a new {@link BlockProperties} instance.
         *
         * @return the properties instance
         */
        public BlockProperties build() {
            return new BlockProperties(
                hardness,
                resistance,
                requireSilkTouch,
                allowFortune,
                isFlammable,
                allowPhysics,
                minExp,
                maxExp,
                pistonReaction
            );
        }
    }
}