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
 */
public class UseContext {
    /** The entity that initiated the item use. */
    private final LivingEntity source;
    /** The block that was clicked, or null if the interaction was in the air or on an entity. */
    private final @Nullable Block targetBlock;
    /** The specific face of the block that was clicked, or null if not applicable. */
    private final @Nullable BlockFace blockFace;
    /** The entity that was clicked, or null if the interaction was on a block or in the air. */
    private final @Nullable Entity targetEntity;
    /** The specific type of click (Right-click or Left-click) performed. */
    private final ClickType type;
    /** The hand (Main or Off-hand) that was used to perform the interaction. */
    private final EquipmentSlot hand;

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
    public UseContext(LivingEntity source, @Nullable Block targetBlock, @Nullable BlockFace blockFace, @Nullable Entity targetEntity, ClickType type, EquipmentSlot hand) {
        this.source = source;
        this.targetBlock = targetBlock;
        this.blockFace = blockFace;
        this.targetEntity = targetEntity;
        this.type = type;
        this.hand = hand;
    }

    /**
     * Gets the entity that used the item.
     *
     * @return The {@link LivingEntity} source.
     */
    public LivingEntity getSource() {
        return source;
    }

    /**
     * Gets the block involved in the interaction.
     *
     * @return The targeted {@link Block}, or {@code null} if no block was targeted.
     */
    public @Nullable Block getTargetBlock() {
        return targetBlock;
    }

    /**
     * Gets the face of the block that was clicked.
     *
     * @return The {@link BlockFace}, or {@code null} if no block was targeted.
     */
    public @Nullable BlockFace getBlockFace() {
        return blockFace;
    }

    /**
     * Gets the entity involved in the interaction.
     *
     * @return The targeted {@link Entity}, or {@code null} if no entity was targeted.
     */
    public @Nullable Entity getTargetEntity() {
        return targetEntity;
    }

    /**
     * Gets the type of click performed.
     *
     * @return The {@link ClickType} (e.g., RIGHT_CLICK or LEFT_CLICK).
     */
    public ClickType getType() {
        return type;
    }

    /**
     * Gets the hand used for the interaction.
     *
     * @return The {@link EquipmentSlot} (HAND or OFF_HAND).
     */
    public EquipmentSlot getHand() {
        return hand;
    }
}