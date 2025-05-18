# Creating a Block

## Step 1: Create a `Block` instance

To define a new block, create an instance of the `Block` class and register it using a `DeferredRegistry<Block>`.

```Java
public static final DeferredRegistry<Block> BLOCKS = DeferredRegistry.create(BuiltinRegistries.BLOCKS, MODID);

public static final DeferredObject<Block> MY_BLOCK = BLOCKS.register("example_block", (name, id) -> new SimpleBlock(id));
```

- MODID is your mods id.
- You MUST extend Block class and make the SimpleBlock (or any other class), as Block doesnt support serialization itself and will cause error spam and fail to load.
- The registered name (`"example_block"`) will be used for the blocks item id (if you override generateItem to return true by lambda), and the blocks own translation key (for other plugins) (e.g `"block.modid.example_block"`).


## Step 2: Apply the registry

apply the registry (call `.apply`) in your onEnable (preferably after items).

```Java
BLOCKS.apply();
```

## Step 3: Add a Texture

Add a texture for your blocks item (not for the block itself) at:

```
src/main/resources/assets/<modid>/textures/item/example_item.png
```

Make sure the filename matches the item's ID exactly.

And that's it! You've registered your first custom block.

next section covers block behaviours (`onInteract`, `onStep`, `onExplode`).