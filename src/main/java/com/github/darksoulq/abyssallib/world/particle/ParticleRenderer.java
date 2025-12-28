package com.github.darksoulq.abyssallib.world.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public interface ParticleRenderer {
    default void start(Location origin) {}
    void render(Location center, List<Vector> points, List<Player> viewers);
    default void stop() {}
}