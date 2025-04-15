# Recipes

In the API, you can register vanilla recipes easily by the provided classes, and can make Custom Recipes by extending the CustomRecipe class.

## Making a Shaped recipe

to make a Shaped Recipe, you need to register an instance of the `ShapedRecipeimpl` class. (Registering follows similar pattern to Items).

```java
public static DeferredRegistry<Recipe> RECIPES = DeferredRegistry.create(BuiltinRegistries.RECIPES, MODID);

public static RegistryObject<Recipe> EXAMPLE_RECIPE = RECIPES.register("name", (name, id) -> 
        new ShapedRecipeImpl(id, Item, " S ", "SSS", " S ")
                .define('S', new ItemStack(Material.STICK)));
```

## Apply the registry

Similar to what was done for items, we apply the RECIPES DeferredRegistry in our `onEnable()`.

```java
RECIPES.apply();
```

---

That's it! you have made a Recipe.

(For Custom Recipes the DeferredRegistry and RegistryObject should be of type CustomRecipe as these cannot be added to bukkit)