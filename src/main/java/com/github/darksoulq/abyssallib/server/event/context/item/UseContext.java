package com.github.darksoulq.abyssallib.server.event.context.item;

import com.github.darksoulq.abyssallib.server.event.ClickType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * A context object representing the details of an item interaction.
 * <p>
 * This class encapsulates all relevant data when an item is used, including the
 * interacting entity, the block or entity targeted, and the hand/click type used.
 *
 * @param source       The entity that initiated the item use.
 * @param targetBlock  The block that was clicked, or null if the interaction was in the air or on an entity.
 * @param blockFace    The specific face of the block that was clicked, or null if not applicable.
 * @param targetEntity The entity that was clicked, or null if the interaction was on a block or in the air.
 * @param type         The specific type of click (Right-click or Left-click) performed.
 * @param hand         The hand (Main or Off-hand) that was used to perform the interaction.
 */
public record UseContext(LivingEntity source, @Nullable Block targetBlock, @Nullable BlockFace blockFace,
                         @Nullable Entity targetEntity, ClickType type, EquipmentSlot hand) {
    /**
     * Constructs a new UseContext with all interaction details.
     *
     * @param source       The {@link LivingEntity} performing the action.
     * @param targetBlock  The {@link Block} being interacted with, if any.
     * @param blockFace    The {@link BlockFace} clicked, if any.
     * @param targetEntity The {@link Entity} being interacted with, if any.
     * @param type         The {@link ClickType} used for this interaction.
     * @param hand         The {@link EquipmentSlot} representing the hand used.
     */
    public UseContext {}

    /**
     * Gets the entity that used the item.
     *
     * @return The {@link LivingEntity} source.
     */
    @Override
    public LivingEntity source() {
        return source;
    }

    /**
     * Gets the block involved in the interaction.
     *
     * @return The targeted {@link Block}, or {@code null} if no block was targeted.
     */
    @Override
    public @Nullable Block targetBlock() {
        return targetBlock;
    }

    /**
     * Gets the face of the block that was clicked.
     *
     * @return The {@link BlockFace}, or {@code null} if no block was targeted.
     */
    @Override
    public @Nullable BlockFace blockFace() {
        return blockFace;
    }

    /**
     * Gets the entity involved in the interaction.
     *
     * @return The targeted {@link Entity}, or {@code null} if no entity was targeted.
     */
    @Override
    public @Nullable Entity targetEntity() {
        return targetEntity;
    }

    /**
     * Gets the type of click performed.
     *
     * @return The {@link ClickType} (e.g., RIGHT_CLICK or LEFT_CLICK).
     */
    @Override
    public ClickType type() {
        return type;
    }

    /**
     * Gets the hand used for the interaction.
     *
     * @return The {@link EquipmentSlot} (HAND or OFF_HAND).
     */
    @Override
    public EquipmentSlot hand() {
        return hand;
    }
}