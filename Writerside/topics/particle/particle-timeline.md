# The Timeline API
<link-summary>Guide to sequencing and chaining multiple particle animations over time</link-summary>

While standalone `Transformer`s apply their logic continuously, the **Timeline API** allows you to sequence multiple animations into specific "keyframes" or time windows. This is essential for creating complex, multi-stage visual effects, such as a shape that grows, pauses, and then spins.

Because `Timeline` itself implements the `Transformer` interface, you can pass it directly into your `Particles` builder just like any other animation.

### Building a Timeline
To create a timeline, use `Timeline.builder()`. You can define a total duration, whether the sequence should loop, and add your specific keyframes.

A keyframe requires a `start` tick, a `duration` in ticks, and the `Transformer` to execute during that window.

```Java
public final class ParticleTest {

    public static void spawnSequence(Player player) {
        // 1. Build the Timeline
        Timeline sequence = Timeline.builder()
            .duration(100) // The timeline lasts exactly 100 ticks (5 seconds)
            .loop(true)    // When it reaches 100 ticks, it restarts at 0
            
            // From tick 0 to 50: Grow the shape from 0x to 2x size
            .add(0, 50, Animations.scale(0.0, 2.0, 50, Easing.EASE_OUT_QUAD))
            
            // From tick 50 to 100: Spin the shape 360 degrees around the Y axis
            .add(50, 50, Animations.spinY(360, 50, Easing.EASE_IN_OUT_CUBIC))
            
            .build();

        // 2. Apply it to the Particles effect
        Particles effect = Particles.builder()
            .origin(player.getLocation())
            .shape(Generators.cube(1.0, 5))
            .render(new Renderers.Standard(Particle.FLAME, 1, 0, null))
            .transform(sequence) // Pass the timeline here!
            .interval(1)
            .build();

        effect.start();
    }
}
```

<tip>
If you add a keyframe that exceeds your configured <code>duration()</code>, the builder will automatically extend the total timeline duration to fit it.
</tip>

### Timeline Builder Methods
<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>duration(long ticks)</code></td>
<td>Sets the total lifespan of the timeline loop in server ticks.</td>
</tr>
<tr>
<td><code>loop(boolean loop)</code></td>
<td>Sets whether the timeline should restart from tick 0 once it completes. Defaults to <code>true</code>.</td>
</tr>
<tr>
<td><code>add(long start, long duration, Transformer transformer)</code></td>
<td>Adds a keyframe to the timeline. The transformer will only apply between <code>start</code> and <code>start + duration</code>.</td>
</tr>
</table>

---

### Billboarding
A common requirement in 3D particle systems is "billboarding"—forcing a flat 2D shape (like a circle or text) to constantly face a specific target, usually the player's camera.

AbyssalLib provides a `Billboarding` utility class to handle this complex vector math for you.

#### Static Billboarding
If your origin and target do not move, use `Billboarding.face()`. The rotation is calculated exactly once when the effect is created, making it highly performant.

```Java
Location origin = new Location(world, 0, 100, 0);
Location target = new Location(world, 10, 100, 10);

Particles.builder()
    .origin(origin)
    .shape(Generators.text("Hello", "Arial", Font.BOLD, 12, 2.0))
    // Calculate rotation once, orienting the text towards the target
    .transform(Billboarding.face(origin, target))
    .render(new Renderers.Standard(Particle.END_ROD, 1, 0, null))
    .build()
    .start();
```

#### Dynamic Billboarding
If your target (or origin) is constantly moving, use `Billboarding.faceDynamic()`. This accepts `Supplier<Location>` parameters and recalculates the rotation vector every single tick.

This is perfect for effects that must always face a moving player.

```Java
Particles.builder()
    .origin(() -> blockLocation)
    .shape(Generators.text("Look at me!", "Arial", Font.BOLD, 12, 2.0))
    // Recalculates rotation every tick to face the player's eye location
    .transform(Billboarding.faceDynamic(
        () -> blockLocation, 
        () -> player.getEyeLocation()
    ))
    .render(new Renderers.Standard(Particle.END_ROD, 1, 0, null))
    .build()
    .start();
```

<note>
When applying multiple transformers, <strong>order matters</strong>. You should generally apply scaling and internal animations (like spinning) <em>before</em> applying a Billboarding or Translation transformer.
</note>