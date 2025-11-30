package com.github.darksoulq.abyssallib.server.event;

import org.bukkit.event.block.Action;

/**
 * Represents the type of click interaction performed by a player.
 * <p>
 * This enum is typically used to distinguish between left and right mouse clicks
 * in custom block or item interactions.
 */
public enum ClickType {

    /**
     * Represents a left-click action.
     */
    LEFT_CLICK,

    /**
     * Represents a right-click action.
     */
    RIGHT_CLICK;

    public static ClickType of(Action type) {
        if (type == Action.LEFT_CLICK_AIR || type == Action.LEFT_CLICK_BLOCK) return LEFT_CLICK;
        if (type == Action.RIGHT_CLICK_BLOCK || type == Action.RIGHT_CLICK_AIR) return RIGHT_CLICK;
        return null;
    }
}
