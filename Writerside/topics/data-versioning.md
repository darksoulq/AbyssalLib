# Data Versioning (DataFixers)
<link-summary>Safely upgrading legacy configuration formats using DataFixers and DataPath queries</link-summary>

As your plugin evolves, your data structures will inevitably change. You might rename configuration keys, flatten nested objects, or introduce entirely new mandatory fields. Instead of cluttering your codecs with complex `if/else` legacy support logic, AbyssalLib provides the `DataFixerRegistry`.

DataFixers operate directly on the raw `DynamicOps` tree *before* your codec attempts to decode it. By registering migrations from version `0` to version `1`, version `1` to `2`, etc., the system automatically upgrades any outdated data sequentially until it reaches your current schema.

### 1. Setting up the Registry
A `DataFixerRegistry` holds the migration logic for specific data types. You instantiate it with your "current" (target) schema version.

```Java
import com.github.darksoulq.abyssallib.common.serialization.fixer.DataFixerRegistry;
import net.kyori.adventure.key.Key;

public class Migrations {
    // The current version of our data schema is 2
    public static final DataFixerRegistry REGISTRY = new DataFixerRegistry(2);
    
    // The unique identifier for this specific data structure
    public static final Key WEAPON_ID = Key.key("myplugin", "weapon_data");
}
```

---

### 2. Deep Intersections via DataPath
Many built-in data fixers accept a path string. Under the hood, these strings are compiled into a structural `DataPath`, allowing you to seamlessly target deeply nested maps, array indices, or a combination of both without manually splitting strings or writing complex tree-navigation loops.

Supported syntax patterns include:
* **Standard Key:** `"inventory"` (Targets a property inside the current root object)
* **Nested Nodes:** `"inventory.weapon.damage"` (Traverses down a tree of nested maps)
* **List Indices:** `"players[3]"` (Targets the element at index 3 inside the `players` list)
* **Root List Index:** `"[0]"` (Targets the first element if the root configuration itself is an array)
* **Complex Chains:** `"players[3].inventory.weapon[0].damage"` (Deep contextual traversal)

---

### 3. Built-in DataFixers
The `DataFixer` interface provides powerful static factory methods to handle data migrations cleanly.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>compose(fixers...)</code></td>
<td>Chains multiple fixers together to run sequentially on the same version step.</td>
</tr>
<tr>
<td><code>renameKey(oldKey, newKey)</code></td>
<td>Renames a specific key within a map structure.</td>
</tr>
<tr>
<td><code>removeKey(key)</code></td>
<td>Deletes a key entirely from the map structure.</td>
</tr>
<tr>
<td><code>addDefault(key, value)</code></td>
<td>Injects a predefined primitive value if the key does not physically exist.</td>
</tr>
<tr>
<td><code>nestKeys(newKey, keysToNest...)</code></td>
<td>Moves explicitly defined sibling keys into a shared child nested map.</td>
</tr>
<tr>
<td><code>hoistKey(nestedKey, keyToHoist)</code></td>
<td>Elevates a deeply nested map key up to the parent map level.</td>
</tr>
<tr>
<td><code>path(pathString, fixer)</code></td>
<td>Translates a compiled <code>DataPath</code> expression (e.g., <code>"stats.damage"</code> or <code>"players[0].stats"</code>) to run a sub-fixer deep inside the tree.</td>
</tr>
<tr>
<td><code>transformValue(key, fixer)</code></td>
<td>Passes a specific map entry's value to a secondary fixer logic.</td>
</tr>
<tr>
<td><code>updateElements(fixer)</code></td>
<td>Executes a sub-fixer across all elements within a List/Array node.</td>
</tr>
<tr>
<td><code>reorderKeys(patterns...)</code></td>
<td>Sorts keys within a map structure based on exact matches, wildcards (<code>*</code>), or regex (<code>~</code>).</td>
</tr>
<tr>
<td><code>conditional(pred, then, else)</code></td>
<td>Redirects data handling based on a dynamic operations predicate.</td>
</tr>
</table>

---

### 4. Registering Migrations
Register your schema changes into your registry. You specify the `fromVersion` that the fixer upgrades the data *from*. Use `DataFixer.path()` whenever you need to apply changes inside objects tucked away within lists or inner structures.

```Java
public class Migrations {
    public static final DataFixerRegistry REGISTRY = new DataFixerRegistry(2);
    public static final Key WEAPON_ID = Key.key("myplugin", "weapon_data");

    static {
        // Upgrade from Version 0 -> Version 1
        // In V1, we renamed 'dmg' to 'damage' and added a mandatory 'durability' field.
        REGISTRY.registerFixer(WEAPON_ID, 0, DataFixer.compose(
            DataFixer.renameKey("dmg", "damage"),
            DataFixer.addDefault("durability", 100)
        ));

        // Upgrade from Version 1 -> Version 2
        // In V2, we restructured data. We nested weapon stats inside an object,
        // and we fixed a deep property within the first slot of an upgrades array.
        REGISTRY.registerFixer(WEAPON_ID, 1, DataFixer.compose(
            DataFixer.nestKeys("stats", "damage", "durability"),
            
            // Target the 'cooldown' key inside the first element of the 'upgrades' list
            DataFixer.path("upgrades[0]", DataFixer.renameKey("old_cd", "cooldown"))
        ));
    }
}
```

---

### 5. Applying the Versioned Modifier
Finally, wrap your standard codec using the `.versioned()` modifier, passing your Key and Registry.

```Java
public static final Codec<WeaponData> CODEC = RecordBuilder.create(instance ->
    instance.group(
        Codecs.STRING.fieldOf("name").forGetter(WeaponData::name),
        Stats.CODEC.fieldOf("stats").forGetter(WeaponData::stats)
    ).apply(instance, WeaponData::new)
).versioned(Migrations.WEAPON_ID, Migrations.REGISTRY);
```

#### How it works internally:
1. **Decoding:** The codec searches the raw data for a `data_version` key. If the version is lower than the target version (or missing, defaulting to `0`), it runs the data through the `DataFixerRegistry` until it hits target version `2`. Then, it strips the `data_version` key and hands the cleanly upgraded map to your underlying `RecordBuilder` codec.
2. **Encoding:** Your object encodes normally. Afterward, the `.versioned()` wrapper automatically appends `"data_version": 2` to the output map so it is safely versioned for the future.