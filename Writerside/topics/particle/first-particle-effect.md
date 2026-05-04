# Creating Your First Particle Effect
<link-summary>Guide to building, animating, and rendering complex particle systems</link-summary>
<secondary-label ref="wip"/>

The Particles API provides a powerful, stateful controller for managing complex, animated 3D particle effects. It utilizes a highly modular builder pattern, allowing you to easily snap together geometric shapes, mathematical animations, and different visual rendering methods.

### Building a Basic Effect
To start off, you will need to create a `Particles` instance using `Particles.builder()`. At a minimum, you must define an origin, a shape, and a renderer.

```Java
public final class ParticleTest {
    
    public static void spawnBasicEffect(Player player) {
        Particles effect = Particles.builder()
            // The central anchor point
            .origin(player.getLocation())
            // A circle with a 2-block radius, made of 30 points
            .shape(Generators.circle(2.0, 30))
            // How to display it
            .render(new Renderers.Standard(Particle.HAPPY_VILLAGER, 1, 0.0, null))
            .build();

        // Starts the asynchronous calculation and rendering loop
        effect.start();
    }
}
```

<note>
Because we didn't specify a <code>duration()</code>, this effect will run indefinitely until you explicitly call <code>effect.stop()</code>.
</note>

[IMG?]

### Dynamic Origins & Animations
You can make effects far more dynamic by passing a `Supplier<Location>` for the origin, which allows the effect to seamlessly follow a moving player or entity. We can also apply Transformers to animate the shape over time.

```Java
public final class ParticleTest {
    
    public static void spawnDynamicEffect(Player player) {
        Particles dynamicEffect = Particles.builder()
            // Origin dynamically updates to the player's current location every tick
            .origin(() -> player.getLocation().add(0, 1, 0))
            // Generate a 3D wireframe cube
            .shape(Generators.cube(2.0, 4))
            // Use the specialized DustRenderer to support custom RGB colors
            .render(new Renderers.DustRenderer(1.0f))
            .color(Color.AQUA)
            // Animate the cube by spinning it around the Y and X axes over 100 ticks
            .transform(Animations.spinY(360, 100, Easing.LINEAR))
            .transform(Animations.spinX(180, 100, Easing.LINEAR))
            // Render every 1 server tick
            .interval(1)
            // Automatically stop after 100 ticks (5 seconds)
            .duration(100)
            .build();

        dynamicEffect.start();
    }
}
```

[VIDEO?]

### Builder Configuration Methods
The `Particles.Builder` contains a wide variety of methods to fine-tune exactly how your effect behaves, renders, and cleans itself up.

<table>
<tr>
<td>Method</td>
<td>Description</td>
</tr>
<tr>
<td><code>origin(Location)</code></td>
<td>Sets a static world location as the central anchor point for the effect.</td>
</tr>
<tr>
<td><code>origin(Supplier&lt;Location&gt;)</code></td>
<td>Sets a dynamic location supplier. Evaluated every tick, allowing the effect to track moving entities.</td>
</tr>
<tr>
<td><code>shape(Generator)</code></td>
<td><strong>(Required)</strong> Sets the geometric shape logic (e.g., <code>Generators.circle()</code>).</td>
</tr>
<tr>
<td><code>render(ParticleRenderer)</code></td>
<td><strong>(Required)</strong> Sets the implementation responsible for displaying the points in the world.</td>
</tr>
<tr>
<td><code>transform(Transformer)</code></td>
<td>Adds a custom spatial modifier to the pipeline (applied sequentially). Useful for complex mathematical animations.</td>
</tr>
<tr>
<td><code>color(Color)</code></td>
<td>Sets a static Bukkit <code>Color</code> for all particles. (Requires a renderer that supports color, like <code>DustRenderer</code>).</td>
</tr>
<tr>
<td><code>color(ColorProvider)</code></td>
<td>Sets a procedural color logic for dynamic, per-particle tinting and gradients.</td>
</tr>
<tr>
<td><code>rotate(double x, double y, double z)</code></td>
<td>Quickly adds a static rotation transformation (in radians) around the respective axes.</td>
</tr>
<tr>
<td><code>scale(double s)</code></td>
<td>Quickly adds a static scaling multiplier transformation to the coordinates.</td>
</tr>
<tr>
<td><code>offset(double x, double y, double z)</code></td>
<td>Quickly adds a static spatial offset transformation to the coordinates.</td>
</tr>
<tr>
<td><code>interval(long ticks)</code></td>
<td>Sets the delay in server ticks between each animation frame. Defaults to <code>1</code>.</td>
</tr>
<tr>
<td><code>duration(long ticks)</code></td>
<td>Sets the total lifetime of the effect in ticks. Set to <code>-1</code> for infinite duration. Defaults to <code>-1</code>.</td>
</tr>
<tr>
<td><code>smooth(boolean)</code></td>
<td>If true, calculates future frame motion vectors for smooth client-side interpolation (creates <code>MotionVector</code>s instead of <code>Pixel</code>s).</td>
</tr>
<tr>
<td><code>viewers(List&lt;Player&gt;)</code></td>
<td>Sets a static list of players who are allowed to see the effect.</td>
</tr>
<tr>
<td><code>viewers(Supplier&lt;List&lt;Player&gt;&gt;)</code></td>
<td>Sets a dynamic supplier for viewers, allowing players to phase in and out of seeing the effect based on conditions.</td>
</tr>
<tr>
<td><code>stopIf(BooleanSupplier)</code></td>
<td>A dynamic condition evaluated every tick. If it returns <code>true</code>, the effect is forcefully terminated.</td>
</tr>
<tr>
<td><code>build()</code></td>
<td>Validates the configuration and produces the final <code>Particles</code> instance. Throws an error if origin, shape, or render are missing.</td>
</tr>
</table>