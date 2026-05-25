# Custom Recipes
<link-summary>Loading, parsing, and defining custom recipe types via JSON/YAML</link-summary>

AbyssalLib provides a `RecipeLoader` that allows you to define standard and custom recipes entirely through JSON or YAML files.
### Loading Recipes
To load recipes, point the `RecipeLoader` to a folder on your server or a resource path inside your plugin's JAR.

```Java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Load default recipes packed inside your plugin's JAR (e.g., src/main/resources/recipes/)
        RecipeLoader.loadResource(this, "recipes/");
        
        // Alternatively, load from a folder in your plugin's data directory
        File recipeDir = new File(getDataFolder(), "recipes");
        if (recipeDir.exists()) {
            RecipeLoader.loadFolder(recipeDir);
        }
    }
}
```

---

### Global Recipe Properties
Every recipe file, regardless of its type, shares a few core properties handled by the loader.

* `id`: (Required) The namespaced key of the recipe (e.g., `myplugin:mythril_sword`).
* `type`: (Required) The registered type of the recipe (e.g., `minecraft:shaped`, `myplugin:crusher`).
* `replace`: (Optional, Default: `false`) If `true`, this recipe will overwrite any existing recipe with the same `id`.
* `disabled`: (Optional, Default: `false`) If `true`, the loader will actively remove the recipe matching the `id` from the server. **This is highly useful for removing vanilla recipes!**

```YAML
# Example: Disabling the vanilla diamond sword recipe
id: "minecraft:diamond_sword"
disabled: true
```

---

### Creating a Custom Recipe Type
If you are building custom crafting mechanics (like a custom Crusher block, an Altar, or a Forge), you can create your own recipe type. This allows server owners to add recipes for your custom machines using the exact same YAML/JSON system.

#### 1. Implement CustomRecipe
First, create a class or record that implements the `CustomRecipe` interface. You must define a `Codec` so the `RecipeLoader` knows how to parse your specific fields.

```Java
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordCodecBuilder;
import com.github.darksoulq.abyssallib.world.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import java.util.Optional;

public record CrusherRecipe(NamespacedKey id, RecipeChoice input, ItemStack result, boolean replace) implements CustomRecipe {

    // Build the Codec to parse the YAML/JSON fields
    public static final Codec<CrusherRecipe> CODEC = RecordCodecBuilder.create(
        Codecs.NAMESPACED_KEY.fieldOf("id", CrusherRecipe::getKey),
        Codecs.RECIPE_CHOICE.fieldOf("input", CrusherRecipe::input),
        Codecs.ITEM_STACK.fieldOf("result", CrusherRecipe::result),
        Codecs.BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        
        // Constructor mapping
        (id, input, result, replace) -> new CrusherRecipe(id, input, result, replace.orElse(false))
    );

    // Define the type reference (we will register this later)
    public static final RecipeType<CrusherRecipe> TYPE = () -> CODEC;

    @Override
    public NamespacedKey getKey() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }
}
```

#### 2. Register the Recipe Type
Once your class is built, register the `RecipeType` into AbyssalLib's recipe type registry.

```Java
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.recipe.RecipeType;

public class MyRecipeTypes {
    // Create a registry under your plugin's namespace
    public static final DeferredRegistry<RecipeType<?>> TYPES = DeferredRegistry.create(Registries.RECIPE_TYPES, "myplugin");

    // Register the custom Crusher recipe type
    public static final RecipeType<?> CRUSHER = TYPES.register("crusher", _ -> CrusherRecipe.TYPE);
}
```

#### 3. Write the YAML
Users can now create files using your completely custom recipe type. The `RecipeLoader` will automatically detect it, parse the `input` and `result` correctly, and store it in the registry for your custom machine to access later.

```YAML
id: "myplugin:crush_cobblestone"
type: "myplugin:crusher"
input: "minecraft:cobblestone"
result:
  id: "minecraft:gravel"
  amount: 1
```