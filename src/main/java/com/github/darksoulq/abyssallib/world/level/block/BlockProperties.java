package com.github.darksoulq.abyssallib.world.level.block;

import com.github.darksoulq.abyssallib.world.level.item.Item;
import com.github.darksoulq.abyssallib.world.level.item.tool.ToolTier;
import com.github.darksoulq.abyssallib.world.level.item.tool.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Objects;
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
     * Whether the block supports the Fortune enchantment to affect drop quantity.
     * Only applies when Silk Touch is not active.
     */
    public final boolean allowFortune;

    /**
     * Set of tool types that break the block faster.
     * Preferred tools do not affect drop validity, only mining speed.
     */
    public final Set<ToolType> preferredTools;

    /**
     * Set of tool types that allow valid drops.
     * If the tool is not correct, the block may break without dropping items.
     */
    public final Set<ToolType> correctTools;

    /**
     * The minimum required tier (e.g., wood, stone, iron) for the tool to break this block and receive drops.
     */
    public final ToolTier requiredTier;

    /**
     * Private constructor used by {@link Builder}.
     *
     * @param hardness the block's hardness
     * @param requireSilkTouch whether Silk Touch is allowed
     * @param allowFortune whether Fortune is allowed
     * @param preferredTools tools that increase breaking speed
     * @param correctTools tools required to obtain drops
     * @param requiredTier minimum tool tier required
     */
    private BlockProperties(
            float hardness,
            boolean requireSilkTouch,
            boolean allowFortune,
            Set<ToolType> preferredTools,
            Set<ToolType> correctTools,
            ToolTier requiredTier
    ) {
        this.hardness = hardness;
        this.requireSilkTouch = requireSilkTouch;
        this.allowFortune = allowFortune;
        this.preferredTools = preferredTools;
        this.correctTools = correctTools;
        this.requiredTier = requiredTier;
    }

    /**
     * Determines if the provided item tier is sufficient.
     *
     * @param stack the item attempting to break the block
     * @return true if the tool tier is equal to or higher than the required tier
     */
    public boolean isTierSufficient(ItemStack stack) {
        Item item = Item.from(stack);
        ToolTier tier = item != null ? item.getTier() : null;
        return tier != null && tier.isAtLeast(requiredTier);
    }

    /**
     * Checks whether the provided tool is valid for dropping items from this block.
     * This is not required for breaking the block, but affects whether drops are generated.
     *
     * @param stack the tool item
     * @return true if the tool type is in the correct tools list
     */
    public boolean isCorrectTool(ItemStack stack) {
        if (correctTools.isEmpty()) return true;
        ToolType type = resolveToolType(stack);
        return correctTools.contains(type) && isTierSufficient(stack);
    }

    /**
     * Checks whether the provided tool is preferred for breaking this block faster.
     *
     * @param stack the tool item
     * @return true if the tool type is in the preferred tools list
     */
    public boolean isPreferredTool(ItemStack stack) {
        if (preferredTools.isEmpty()) return true;
        ToolType type = resolveToolType(stack);
        return preferredTools.contains(type);
    }

    /**
     * Resolves the {@link ToolType} from an ItemStack.
     * Falls back to guessing based on Bukkit material if the item is not a custom {@link Item}.
     *
     * @param stack the item stack
     * @return the resolved tool type, or {@code null} if none can be determined
     */
    private ToolType resolveToolType(ItemStack stack) {
        Item item = Item.from(stack);
        if (item != null && item.getToolType() != null) return item.getToolType();
        Material material = stack.getType();
        return ToolType.fromMaterial(material);
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
        private final Set<ToolType> preferredTools = EnumSet.noneOf(ToolType.class);
        private final Set<ToolType> correctTools = EnumSet.noneOf(ToolType.class);
        private ToolTier requiredTier = new ToolTier("none", 0);

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
         * Adds a preferred tool type (for increased break speed).
         *
         * @param type the tool type
         * @return this builder
         */
        public Builder preferredTool(ToolType type) {
            this.preferredTools.add(Objects.requireNonNull(type));
            return this;
        }

        /**
         * Adds a correct tool type (required to receive drops).
         *
         * @param type the tool type
         * @return this builder
         */
        public Builder correctTool(ToolType type) {
            this.correctTools.add(Objects.requireNonNull(type));
            return this;
        }

        /**
         * Sets the minimum required tool tier.
         *
         * @param tier the tool tier
         * @return this builder
         */
        public Builder requiredTier(ToolTier tier) {
            this.requiredTier = Objects.requireNonNull(tier);
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
                    allowFortune,
                    Set.copyOf(preferredTools),
                    Set.copyOf(correctTools),
                    requiredTier
            );
        }
    }
}
