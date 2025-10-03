# Creating Shapes

<warning>
In the case you choose to use shapes methods like <code>count</code> will be disregarded.
</warning>

### Creating your own shapes
```Java
public static Shape circle(double radius, int points) {
        return (origin, tick, particles) -> {
            List<Location> list = particles.getLocationBuffer(points);
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                list.set(i, particles.poolLocation(origin.getX() + x, origin.getY(), origin.getZ() + z));
            }
            return list;
        };
    }
```

The above code creates a basic circle shape which takes in radius and points, as you may have noticed it is created by a lamba, however you can do just the same by implementing the Shape interface.

> For locations, as shown in the code above it is recommended to use the `getLocationBuffer` and `poolLocation` methods of `Particles` as it allows for more performant operations.
> - `poolLocation` gets a Location of a specific world from the Location Buffer and sets it x, y, z to the provided values.
> - `getLocationBuffer` returns a List of usable Location objects by the amount provided from the origin world of the `Particles`.

Once you create it using it is as simple as:
```Java
public class MyClass {
    public static Particles createParticle() {
        Particles particle = Particles.builder()
                .particle(Particle.NOTE)
                .spawnAt(Location)
                .shape(circle(5.5, 10))
                .build();
        particle.start();
        return particle;
    }
}
```

In case you build it with thread safety in mind (using the location buffer should suffice in most cases) you can also apply `asyncShape(true)` to the particle

### Example Animated Rotating Circle:
```Java
public static Shape rotatingCircle(double radius, int points, double rotationSpeed) {
    return (origin, tick, builder) -> {
        List<Location> buffer = builder.getLocationBuffer(points);
        double angleOffset = (tick * rotationSpeed) % (2 * Math.PI);
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points + angleOffset;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            buffer.set(i, builder.poolLocation(origin.getX() + x, origin.getY(), origin.getZ() + z));
        }
        return buffer;
    };
}
```