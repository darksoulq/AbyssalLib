# Creating Items

## Step 1: Create an `Item` instance

To define a new item, create an instance of the `Item` class and register it using a `DeferredRegistry<Item>`.

```java
public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(BuiltinRegistries.ITEMS, MODID);

public static final RegistryObject<Item> MAGIC_WAND = ITEMS.register("example_item", (name, id) -> new Item(id, Material.DIAMOND));
```

- `MODID` is your mod ID.
- The registered name (`"example_item"`) will be used for the item's ID and texture name (`item.modid.item_name`, and `textures/item/item_name.png`).
- You can customize the item with the `new ItemSettings(item)` builder (e.g., lore, behaviors, etc.).

## Step 2: Apply the Registry

In your plugin's `onEnable()` method, make sure to apply the registry:

```java
ITEMS.apply();
```

This finalizes the registry and registers all your items into the game.

## Step 3: Add a Texture

Add a texture for your item at:

```
src/main/resources/assets/<modid>/textures/item/example_item.png
```

Make sure the filename matches the item's ID exactly.

## Example

Your plugin might look like this:

```java
public final class Plugin extends JavaPlugin {
    @Override
    public void onEnable() {
        ITEMS.apply();
        new Resourcepack(this, "mymod").generate();
    }
}
```

And that's it! You've registered your first custom item.

next section covers item behaviours (`onRightClick`, `onLeftClick`, `onUseEntity`).
