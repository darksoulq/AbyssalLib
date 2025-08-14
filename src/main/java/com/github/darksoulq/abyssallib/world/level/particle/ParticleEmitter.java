package com.github.darksoulq.abyssallib.world.level.particle;

import org.bukkit.Location;

/**
 * A provider for dynamic particle origin locations.
 * Used by {@link Particles.Builder#spawnAt(ParticleEmitter)} to allow moving particle effects.
 */
@FunctionalInterface
public interface ParticleEmitter {
    /**
     * Returns the current location to spawn particles at.
     *
     * @return the current origin location
     */
    Location getLocation();
}
