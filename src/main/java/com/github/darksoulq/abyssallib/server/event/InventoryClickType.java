package com.github.darksoulq.abyssallib.server.event;

import org.bukkit.event.inventory.ClickType;

/**
 * A wrapper enum for Bukkit's ClickType to avoid import conflicts.
 */
public enum InventoryClickType {

    LEFT(ClickType.LEFT),
    SHIFT_LEFT(ClickType.SHIFT_LEFT),
    RIGHT(ClickType.RIGHT),
    SHIFT_RIGHT(ClickType.SHIFT_RIGHT),
    WINDOW_BORDER_LEFT(ClickType.WINDOW_BORDER_LEFT),
    WINDOW_BORDER_RIGHT(ClickType.WINDOW_BORDER_RIGHT),
    MIDDLE(ClickType.MIDDLE),
    NUMBER_KEY(ClickType.NUMBER_KEY),
    DOUBLE_CLICK(ClickType.DOUBLE_CLICK),
    DROP(ClickType.DROP),
    CONTROL_DROP(ClickType.CONTROL_DROP),
    CREATIVE(ClickType.CREATIVE),
    SWAP_OFFHAND(ClickType.SWAP_OFFHAND),
    UNKNOWN(ClickType.UNKNOWN);

    private final ClickType bukkit;

    InventoryClickType(ClickType bukkit) {
        this.bukkit = bukkit;
    }

    /**
     * Converts from Bukkit ClickType to InventoryClickType.
     */
    public static InventoryClickType of(ClickType bukkit) {
        for (InventoryClickType type : values()) {
            if (type.bukkit == bukkit) return type;
        }
        return UNKNOWN;
    }

    /**
     * Gets the underlying Bukkit ClickType.
     */
    public ClickType asBukkit() {
        return bukkit;
    }

    public boolean isKeyboardClick() {
        return bukkit.isKeyboardClick();
    }

    public boolean isMouseClick() {
        return bukkit.isMouseClick();
    }

    public boolean isCreativeAction() {
        return bukkit.isCreativeAction();
    }

    public boolean isRightClick() {
        return bukkit.isRightClick();
    }

    public boolean isLeftClick() {
        return bukkit.isLeftClick();
    }

    public boolean isShiftClick() {
        return bukkit.isShiftClick();
    }
}
