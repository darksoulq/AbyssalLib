package com.github.darksoulq.abyssallib.world.particle.timeline;

import com.github.darksoulq.abyssallib.world.particle.Transformer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Supplier;

/**
 * A utility class for creating billboarding and orientation {@link Transformer}s.
 * <p>
 * Billboarding allows particle shapes (which are typically generated on a flat plane)
 * to be rotated so that they "face" a specific target, such as a player's camera
 * or a specific world coordinate.
 */
public class Billboarding {

    /**
     * Creates a static transformer that rotates a shape's local "up" (0, 1, 0)
     * to face a specific target location.
     * <p>
     * The rotation is calculated once when this method is called and remains
     * constant regardless of subsequent movements of the origin or target.
     *
     * @param origin The starting {@link Location} of the particle effect.
     * @param target The {@link Location} the shape should be oriented towards.
     * @return A {@link Transformer} that applies the static rotation.
     */
    public static Transformer face(Location origin, Location target) {
        Vector direction = target.clone().subtract(origin).toVector().normalize();
        Vector up = new Vector(0, 1, 0);
        Vector axis = up.getCrossProduct(direction).normalize();
        double angle = Math.acos(up.dot(direction));

        return (v, tick) -> rotateAroundAxis(v, axis, angle);
    }

    /**
     * Creates a dynamic transformer that continuously re-orients a shape
     * to face a moving target.
     * <p>
     * This is ideal for effects that must always face a specific {@link org.bukkit.entity.Player}
     * as they move around the effect.
     *
     * @param originSupplier A {@link Supplier} providing the current origin {@link Location}.
     * @param targetSupplier A {@link Supplier} providing the current target {@link Location}.
     * @return A dynamic {@link Transformer} that recalculates orientation every tick.
     */
    public static Transformer faceDynamic(Supplier<Location> originSupplier, Supplier<Location> targetSupplier) {
        return (v, tick) -> {
            Location origin = originSupplier.get();
            Location target = targetSupplier.get();
            if (origin == null || target == null) return v;

            Vector direction = target.clone()
                .subtract(origin)
                .toVector()
                .normalize();

            Vector up = new Vector(0, 1, 0);
            Vector axis = up.getCrossProduct(direction).normalize();
            double angle = Math.acos(up.dot(direction));

            return rotateAroundAxis(v, axis, angle);
        };
    }

    /**
     * Rotates a vector around a given axis by a specific angle using Rodrigues' rotation formula.
     * <p>
     * Formula: $v_{rot} = v \cos \theta + (e \times v) \sin \theta + e(e \cdot v)(1 - \cos \theta)$
     *
     * @param v     The original {@link Vector} coordinate.
     * @param axis  The unit {@link Vector} representing the axis of rotation.
     * @param angle The angle of rotation in radians.
     * @return The rotated {@link Vector}.
     */
    private static Vector rotateAroundAxis(Vector v, Vector axis, double angle) {
        if (Double.isNaN(axis.getX())) return v;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dot = v.dot(axis);

        return v.clone().multiply(cos)
            .add(axis.clone().crossProduct(v).multiply(sin))
            .add(axis.clone().multiply(dot * (1 - cos)));
    }
}