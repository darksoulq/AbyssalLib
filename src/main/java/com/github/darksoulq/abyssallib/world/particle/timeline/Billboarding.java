package com.github.darksoulq.abyssallib.world.particle.timeline;

import com.github.darksoulq.abyssallib.world.particle.Transformer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Supplier;

public class Billboarding {
    /**
     * Rotates the shape so its local "up" direction (positive Y axis)
     * faces the target location.
     *
     * <p>
     * The rotation is static: it is computed once from the supplied locations
     * and does not change over time.
     * </p>
     *
     * @param origin the origin location of the shape
     * @param target the target location to face
     * @return a {@link Transformer}
     */
    public static Transformer face(Location origin, Location target) {
        Vector direction = target.clone().subtract(origin).toVector().normalize();
        Vector up = new Vector(0, 1, 0);
        Vector axis = up.getCrossProduct(direction).normalize();
        double angle = Math.acos(up.dot(direction));

        return (v, tick) -> rotateAroundAxis(v, axis, angle);
    }

    /**
     * Creates a dynamic billboarding transformer that continuously faces
     * a moving target.
     *
     * <p>
     * This variant recalculates the facing direction every tick and is suitable
     * for players or entities that move.
     * </p>
     *
     * @param originSupplier supplier providing the current origin location
     * @param targetSupplier supplier providing the current target location
     * @return a {@link Transformer}
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