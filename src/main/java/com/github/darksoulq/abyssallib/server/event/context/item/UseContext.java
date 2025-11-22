package com.github.darksoulq.abyssallib.server.event.context.item;

import com.github.darksoulq.abyssallib.server.event.ClickType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class UseContext {
    private final LivingEntity source;
    private final @Nullable Block targetBlock;
    private final @Nullable BlockFace blockFace;
    private final @Nullable Entity targetEntity;
    private final ClickType type;
    private final EquipmentSlot hand;

    public UseContext(LivingEntity source, @Nullable Block targetBlock, @Nullable BlockFace blockFace, @Nullable Entity targetEntity, ClickType type, EquipmentSlot hand) {
        this.source = source;
        this.targetBlock = targetBlock;
        this.blockFace = blockFace;
        this.targetEntity = targetEntity;
        this.type = type;
        this.hand = hand;
    }

    public LivingEntity getSource() {
        return source;
    }
    public @Nullable Block getTargetBlock() {
        return targetBlock;
    }
    public @Nullable BlockFace getBlockFace() {
        return blockFace;
    }
    public @Nullable Entity getTargetEntity() {
        return targetEntity;
    }
    public ClickType getType() {
        return type;
    }
    public EquipmentSlot getHand() {
        return hand;
    }
}
