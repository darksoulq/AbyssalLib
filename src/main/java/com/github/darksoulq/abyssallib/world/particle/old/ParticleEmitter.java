package com.github.darksoulq.abyssallib.world.particle.old;

import org.bukkit.Location;

/**
 * A provider for dynamic particle origin locations.
 * Used by {@link Particles.Builder#spawnAt(ParticleEmitter)} to allow moving particle effects.
 * @deprecated No longer used in new API, Supplier is directly used now
 */
@Deprecated(forRemoval = true, since = "v1.8.0-mc1.21.9")
@FunctionalInterface
public interface ParticleEmitter {
    /**
     * Returns the current location to spawn particles at.
     *
     * @return the current origin location
     */
    Location getLocation();
}
