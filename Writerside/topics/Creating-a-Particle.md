# Creating a Particle

> As mentioned in the [](Getting-Started.md) AbyssalLib provides a Builder-Style Particle API.

### Creating a simple particle
```Java
public class MyClass {
    public static Particles createParticle() {
        Particles particle = Particles.builder()
                .particle(Particle.NOTE)
                .spawnAt(Location)
                // OR .spawnAt(Player::getLocation) for dynamic location (you could also just modify the provided location object)
                .build();
        particle.start();
        return particle;
    }
}
```

This creates a simple `Note` particle at the provided `Location`.

> There are also some optional methods:
> - `count(int count)`
> - `offset(double x, double y, double z)`
> - `speed(double speed)`
> - `shape(Shape shape)` (will be discussed in next section)
> - `data(Object data)` (for providing particle data)
> - `interval(long ticks)` (delay between particle respawns)
> - `duration(long ticks)` (-1 for infinite)
> - `cancelIf(BooleanSupplier s)` (stops if condition is true)
> - `viewers(List<Player> vs)` (whom to show it to, if not specified, shows to all)
> - `viewers(Player single)`
> - `asyncShape(boolean v)` (whether shape should be handled Async)

### Creating a particle using an ItemStack
```Java
public class MyClass {
    public static Particles createParticle() {
        Particles particle = Particles.builder()
                .display(ItemStack)
                .spawnAt(Location)
                // OR .spawnAt(Player::getLocation) for dynamic location (you could also just modify the provided location object)
                .build();
        particle.start();
        return particle;
    }
}
```

> These are handled similarly to normal particles, however also have some unique parameters (including previously mentioned methods)
> - `scale(float scale)`
> - `scale(float x, float y, float z)`
> - `billboard(Display.Billboard b)`
> - `rotation(float xDeg, float yDeg, float zDeg)`