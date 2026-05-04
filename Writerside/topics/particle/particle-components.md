# Custom Particle Components
<link-summary>Guide to creating custom Generators, Transformers, and Renderers</link-summary>

If the default particle components do not meet your exact needs, the Particles API is designed to be easily extensible. You can create custom shapes, animations, and visualization methods by implementing the respective interfaces.

### Custom Generator
The `Generator` is a functional interface responsible for providing a list of relative coordinates (vectors) for a specific animation tick.

Because it is a functional interface, you can pass lambdas directly into the builder, or create a dedicated class for complex mathematical shapes.

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>generate(long tick)</code></td>
<td>Returns a <code>List&lt;Vector&gt;</code> representing the local coordinates of the shape for the given tick.</td>
</tr>
</table>

**Example: A Random Scatter Generator**
This generator produces a specified number of completely random points within a bounded box every tick.

```Java
public class ScatterGenerator implements Generator {
    
    private final int count;
    private final double spread;

    public ScatterGenerator(int count, double spread) {
        this.count = count;
        this.spread = spread;
    }

    @Override
    public List<Vector> generate(long tick) {
        List<Vector> points = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            double x = (Math.random() - 0.5) * spread;
            double y = (Math.random() - 0.5) * spread;
            double z = (Math.random() - 0.5) * spread;
            
            // You can optionally return Pixel objects if you want to assign colors!
            points.add(new Vector(x, y, z));
        }
        return points;
    }
}
```

---

### Custom Transformer
The `Transformer` is a functional interface that intercepts the vectors produced by the `Generator` and modifies them before they reach the renderer. This is typically used for spatial manipulation (rotation, scale, offsets).

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>transform(Vector input, long tick)</code></td>
<td>Takes an input vector and the current tick, and returns the modified vector.</td>
</tr>
</table>

**Example: A Jitter/Noise Transformer**
This transformer applies a slight, randomized offset to every point, simulating a chaotic "shaking" or noise effect.

```Java
public class JitterTransformer implements Transformer {
    
    private final double intensity;

    public JitterTransformer(double intensity) {
        this.intensity = intensity;
    }

    @Override
    public Vector transform(Vector input, long tick) {
        double offsetX = (Math.random() - 0.5) * intensity;
        double offsetY = (Math.random() - 0.5) * intensity;
        double offsetZ = (Math.random() - 0.5) * intensity;
        
        // Always modify and return the vector
        return input.add(new Vector(offsetX, offsetY, offsetZ));
    }
}
```

<tip>
Because <code>Transformer</code> is a functional interface, you can apply simple logic directly in the builder:
<code>.transform((v, tick) -> v.add(new Vector(0, Math.sin(tick / 10.0), 0)))</code>
</tip>

---

### Custom Renderer
The `ParticleRenderer` interface is responsible for taking the final, mathematically processed coordinates and displaying them in the Minecraft world.

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>start(Location origin)</code></td>
<td><em>(Optional)</em> Fires when the effect begins. Useful for spawning persistent entities or playing a startup sound.</td>
</tr>
<tr>
<td><code>render(Location center, List&lt;Vector&gt; points, List&lt;Player&gt; viewers)</code></td>
<td>Fires every tick. Responsible for iterating through the points and executing the visual display logic.</td>
</tr>
<tr>
<td><code>stop()</code></td>
<td><em>(Optional)</em> Fires when the effect is terminated. Must be used to clean up any persistent entities or memory leaks.</td>
</tr>
</table>

**Example: A Sound Renderer**
Renderers do not strictly have to spawn particles. This custom renderer plays a subtle note block sound at the location of every generated point, creating spatial audio effects.

```Java
import com.github.darksoulq.abyssallib.world.particle.ParticleRenderer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class SoundRenderer implements ParticleRenderer {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundRenderer(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void render(Location center, List<Vector> points, List<Player> viewers) {
        if (center.getWorld() == null || points.isEmpty()) return;

        for (Vector v : points) {
            // Calculate the absolute world location of the point
            Location loc = center.clone().add(v);
            
            // Play the sound
            if (viewers == null || viewers.isEmpty()) {
                loc.getWorld().playSound(loc, sound, volume, pitch);
            } else {
                for (Player p : viewers) {
                    p.playSound(loc, sound, volume, pitch);
                }
            }
        }
    }
}
```

<note>
If your renderer spawns entities (like Armor Stands or Block Displays), you <strong>must</strong> store references to their Entity IDs/UUIDs and override the <code>stop()</code> method to <code>remove()</code> them when the effect ends, otherwise you will cause severe entity leaking.
</note>