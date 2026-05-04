# Expressionable Placeholders
<link-summary>Guide to creating placeholders that support logical and mathematical operations</link-summary>

The `Expressionable<T>` interface allows placeholders to be dynamically evaluated using mathematical and logical operators directly from text tags. This eliminates the need to create dozens of specific placeholders for simple comparisons or arithmetic.

### Available Operations
When a placeholder implements `Expressionable`, it can be targeted by the global `abyssallib` operator placeholders.

The standard syntax for an operation is: `<placeholder:abyssallib:<operator>:<target_placeholder>:[argument]>`

<table>
<tr>
<th>Operator</th>
<th>Category</th>
<th>Description</th>
</tr>
<tr>
<td><code>add</code>, <code>sub</code>, <code>mul</code>, <code>div</code>, <code>mod</code>, <code>pow</code></td>
<td>Mathematics</td>
<td>Standard arithmetic operations. Requires an argument.</td>
</tr>
<tr>
<td><code>min</code>, <code>max</code></td>
<td>Mathematics</td>
<td>Returns the minimum or maximum between the target and the argument.</td>
</tr>
<tr>
<td><code>round</code>, <code>floor</code>, <code>ceil</code>, <code>abs</code></td>
<td>Math (Unary)</td>
<td>Standard rounding and absolute value operations. Does not take an argument.</td>
</tr>
<tr>
<td><code>sin</code>, <code>cos</code>, <code>tan</code>, <code>asin</code>, <code>acos</code>, <code>atan</code></td>
<td>Trigonometry</td>
<td>Standard trigonometric functions. Does not take an argument.</td>
</tr>
<tr>
<td><code>eq</code>, <code>neq</code>, <code>gt</code>, <code>lt</code>, <code>gte</code>, <code>lte</code></td>
<td>Relational Logic</td>
<td>Equality and greater/less-than comparisons. Returns a boolean. Requires an argument.</td>
</tr>
<tr>
<td><code>and</code>, <code>or</code>, <code>xor</code></td>
<td>Boolean Logic</td>
<td>Standard logical operators. Returns a boolean. Requires an argument.</td>
</tr>
<tr>
<td><code>not</code></td>
<td>Boolean (Unary)</td>
<td>Inverts the boolean state of the target. Does not take an argument.</td>
</tr>
</table>

### Implementing a Custom Expressionable
While AbyssalLib provides abstract classes for basic types (`AbstractDoublePlaceholder`, `AbstractStringPlaceholder`, `AbstractBooleanPlaceholder`), you can implement `Expressionable<T>` on any custom placeholder.

For this example, we will create a `PlayerVelocityPlaceholder` that returns a Bukkit `Vector`. We will implement `add` to combine velocities, and `gt` (greater than) to compare the magnitude of the velocity against a threshold.

```Java
import com.github.darksoulq.abyssallib.server.placeholder.Placeholder;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderArgument;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderContext;
import com.github.darksoulq.abyssallib.server.placeholder.PlaceholderResult;
import com.github.darksoulq.abyssallib.server.placeholder.expression.Expressionable;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerVelocityPlaceholder extends Placeholder<Vector> implements Expressionable<Vector> {

    public PlayerVelocityPlaceholder(Key id) {
        super(id, Vector.class);
    }

    @Override
    public PlaceholderResult<Vector> resolve(PlaceholderContext context) {
        Player player = context.getPlayer();
        if (player == null) return PlaceholderResult.empty();
        
        return PlaceholderResult.success(player.getVelocity());
    }

    @Override
    public PlaceholderResult<Vector> add(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Vector> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return res;

        // Attempt to parse the argument as a single scalar double to add to all axes
        Double scalar = other.asDouble().getOrNull();
        if (scalar != null) {
            Vector current = res.getValue().clone();
            return PlaceholderResult.success(current.add(new Vector(scalar, scalar, scalar)));
        }
        
        return PlaceholderResult.error("Invalid vector addition argument");
    }

    @Override
    public PlaceholderResult<Boolean> gt(PlaceholderContext ctx, PlaceholderArgument other) {
        PlaceholderResult<Vector> res = resolve(ctx);
        if (res.isEmpty() || res.isError()) return PlaceholderResult.empty();

        // Compare the vector's length (speed) against a provided double
        Double threshold = other.asDouble().getOrNull();
        if (threshold != null) {
            return PlaceholderResult.success(res.getValue().length() > threshold);
        }

        return PlaceholderResult.empty();
    }

    // ... (Other Expressionable methods must be implemented, returning PlaceholderResult.empty() if unsupported) ...

    @Override
    public PlaceholderResult<Vector> sub(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Vector> mul(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    @Override public PlaceholderResult<Vector> div(PlaceholderContext ctx, PlaceholderArgument other) { return PlaceholderResult.empty(); }
    
    // ... remaining methods omitted for brevity
}
```

### Using Expressions in Text
Once your expressionable placeholder is registered, you can target it using the global `abyssallib` operator placeholders.

Assuming the `PlayerVelocityPlaceholder` is registered as `abyssallib_example:velocity`, here is how it is used in text:

* **Basic Resolution:** Displays the vector directly (falling back to its `toString()` or your custom `format()` method).
  `<placeholder:abyssallib_example:velocity>`
* **Math Expression:** Adds `0.5` to all axes of the player's current velocity.
  `<placeholder:abyssallib:add:abyssallib_example:velocity:0.5>`
* **Logic Expression:** Evaluates to `true` if the player's total velocity magnitude is greater than `2.0`.
  `<placeholder:abyssallib:gt:abyssallib_example:velocity:2.0>`