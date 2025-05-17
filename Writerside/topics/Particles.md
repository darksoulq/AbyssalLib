# Particles

AbyssalLib provides a simple yet powerful API for working with particle effects in Bukkit. It supports both freeform particle emissions and structured shapes, with support for animation, custom timing, and conditional cancellation.

## Basic usage:
Spawn simple particles at a location:
```Java
Particles.of(Particle.FLAME)
    .spawnAt(player.getLocation())
    .withCount(10)
    .withOffset(0.2, 0.5, 0.2)
    .withSpeed(0.01)
    .start();
```

## Shapes & animations:
Use the usingShape(...) method to display particles in predefined or custom geometric patterns. Built-in animated variants also support dynamic movement.

Built-in Shapes (`Shapes`)
- `circle(radius, points)`
- `sphere(radius, points)`
- `cube(size)`
- `pyramid(height, points)`
```Java
Particles.of(Particle.CRIT)
    .spawnAt(location)
    .usingShape(Shapes.circle(1.5, 30))
    .start();

```

Animated Shapes (`AnimatedShapes`)
- `rotatingCircle(radius, points, rotationSpeed)`
- `rotatingSphere(radius, points, rotationSpeed)`

```Java
Particles.of(Particle.END_ROD)
    .spawnAt(location)
    .usingShape(AnimatedShapes.rotatingCircle(2, 50, 0.1))
    .every(2)
    .duration(200)
    .start();
```

## Repetition and Timing:
Control how often and how long the effect runs:
- `.every(ticks)` — Sets how often the effect updates (default: `1` tick)
- `.duration(ticks)` — Total duration of the effect. Use `-1` to run forever.

```Java
Particles.of(Particle.HEART)
    .spawnAt(location)
    .usingShape(Shapes.pyramid(2, 16))
    .every(10)
    .duration(100)
    .start();
```

## Conditional Cancellation:
You can stop the effect dynamically using a condition:
```Java
Particles.particle(Particle.FLAME)
    .at(location)
    .cancelIf(() -> !player.isOnline()) // cancels if the player logs out
    .start();
```

## Extending with Custom Shapes:
You can define your own Shape by implementing the interface:
```Java
Shape spiral = origin -> {
    List<Location> points = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
        double t = i / 10.0;
        double x = Math.cos(t) * t;
        double z = Math.sin(t) * t;
        double y = t * 0.1;
        points.add(origin.clone().add(x, y, z));
    }
    return points;
};

Particles.of(Particle.SMOKE_NORMAL)
    .spawnAt(location)
    .usingShape(spiral)
    .start();
```
To animate the shape, override `animate(...)` as well:
```Java
Shape animated = new Shape() {
    double angle = 0;

    @Override
    public List<Location> points(Location origin) {
        List<Location> result = new ArrayList<>();
        double x = Math.cos(angle);
        double z = Math.sin(angle);
        result.add(origin.clone().add(x, 0, z));
        return result;
    }

    @Override
    public void animate(Particles builder, Location origin, long tick) {
        angle += 0.05;
    }
};
```