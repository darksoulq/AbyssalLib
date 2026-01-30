package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Interface responsible for the actual display of calculated particle points in the world.
 * <p>
 * Implementations can use vanilla particle packets, custom entities, or
 * third-party plugin APIs to display the effect.
 */
public interface ParticleRenderer {
    /**
     * Called when the effect is initialized.
     * Useful for pre-calculating data or setting up visual states.
     *
     * @param origin The starting world {@link Location}.
     */
    default void start(Location origin) {}

    /**
     * Executes the visual rendering of the particle frame.
     *
     * @param center  The central {@link Location} to offset the points from.
     * @param points  The list of processed {@link Vector} coordinates.
     * @param viewers The list of players who should receive the display,
     * or {@code null} for global visibility.
     */
    void render(Location center, List<Vector> points, List<Player> viewers);

    /**
     * Called when the effect stops.
     * Useful for removing persistent entities or cleaning up temporary visual resources.
     */
    default void stop() {}
}