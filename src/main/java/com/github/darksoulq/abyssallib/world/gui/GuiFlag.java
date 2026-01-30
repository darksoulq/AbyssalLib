package com.github.darksoulq.abyssallib.world.gui;

/**
 * Flags used to configure and restrict the behavior of a {@link Gui}.
 * <p>
 * These constants are checked by the GUI event system to determine whether
 * certain interactions should be handled by the API, ignored, or blocked
 * entirely.
 */
public enum GuiFlag {

    /**
     * Instructs the GUI API to ignore all interactions in the top (menu) inventory.
     * <p>
     * When set, the {@link GuiElement#onClick} and {@link GuiElement#onDrag}
     * handlers will not be triggered for the top segment, allowing for
     * vanilla-default behavior or custom external handling.
     */
    DISABLE_TOP,

    /**
     * Instructs the GUI API to ignore all interactions in the bottom (player) inventory.
     * <p>
     * When set, elements defined for the player's inventory will not have
     * their interaction handlers executed.
     */
    DISABLE_BOTTOM,

    /**
     * Prevents the player from picking up items from the ground while
     * this GUI is open.
     */
    DISABLE_ITEM_PICKUP,

    /**
     * Prevents the player from progressing in or being granted
     * advancement criteria while this GUI is open.
     */
    DISABLE_ADVANCEMENTS
}