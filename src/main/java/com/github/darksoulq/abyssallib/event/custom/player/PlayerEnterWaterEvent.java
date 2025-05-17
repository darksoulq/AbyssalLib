package com.github.darksoulq.abyssallib.event.custom.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event fired when a player falls into water.
 */
public class PlayerEnterWaterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Location location;
    private final Vector velocity;

    public PlayerEnterWaterEvent(Player player, Location location, Vector velocity) {
        this.player = player;
        this.location = location;
        this.velocity = velocity;
    }

    /**
     * Gets the player who fell into water.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the location where the player entered the water.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the player's velocity when entering the water.
     *
     * @return the velocity vector
     */
    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
