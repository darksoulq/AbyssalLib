# Particles

The library provides a really simple but powerful particle system, the main concept is Shapes (shapeless particle spawns ARE possible!).

## Basic usage:
```Java
Particles.particle(Particle.FLAME)
    .at(player.getLocation())
    .count(10)
    .offset(0.2, 0.5, 0.2)
    .speed(0.01)
    .start();
```

## Shapes & animations:
The `shape(...)` method allows you to use predefined or custom particle formations. You can also use animated shapes like rotating circles or spheres.
Built-in Shapes `(Shapes)`
- `circle(radius, points)`
- `sphere(radius, points)`
- `cube(size)`
- `pyramid(height, points)`

```Java
Particles.particle(Particle.CRIT)
    .at(location)
    .shape(Shapes.circle(1.5, 30))
    .start();
```

Animated Shapes `(AnimatedShapes)`
- `rotatingCircle(radius, points, rotationSpeed)`
- `rotatingSphere(radius, points, rotationSpeed)`

```Java
Particles.particle(Particle.END_ROD)
    .at(location)
    .shape(AnimatedShapes.rotatingCircle(2, 50, 0.1))
    .everyTicks(2)
    .repeat(200)
    .start();
```

## Repetition and Timing:
You can control how often the particle effect updates and how long it runs.

- `.everyTicks(ticks)` — Set how often to update (default: `1` tick).
- `.repeat(ticks)` — Set total duration. Use `-1` to run indefinitely.

```Java
Particles.particle(Particle.HEART)
    .at(location)
    .shape(Shapes.pyramid(2, 16))
    .everyTicks(10)
    .repeat(100)
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

Particles.particle(Particle.SMOKE_NORMAL)
    .at(location)
    .shape(spiral)
    .start();
```
To animate the shape, override `animate(...)` as well:
```Java
Shape animated = new Shape() {
    double angle = 0;

    @Override
    public List<Location> points(Location origin) {
        // Generate based on angle
    }

    @Override
    public void animate(Particles builder, Location origin, long tick) {
        angle += 0.05;
    }
};
```