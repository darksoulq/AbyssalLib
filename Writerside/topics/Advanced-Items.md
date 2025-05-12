# Advanced Items: Custom Behavior

You can give your items custom functionality by subclassing the `Item` class and overriding behavior hooks like `onRightClick`, `onUseOnBlock`, etc.

These methods are automatically called by the Library when players use your item in the corresponding context.

---

## Step 1: Extend the `Item` class

Create a new class that extends `Item`, and override the methods you want to customize.

```java
public class TeleportStick extends Item {
    public TeleportStick(ResourceLocation id) {
        super(id, Material.STICK);
    }

    @Override
    public void onRightClick(ItemUseContext ctx) {
        ctx.player.teleport(ctx.player().getLocation().add(0, 10, 0));
        ctx.player.sendMessage(Component.text("Whoosh!"));
    }
}
```

---

##  Step 2: Register Your Custom Item

You can now register your item the same way as normal items (but only pass in id in this case).

```java
public static final RegistryObject<Item> TELEPORT_STICK = ITEMS.register("teleport_stick", (name, id) ->
    new TeleportStick(id)
);
```

---

## Other Overridable Methods

You can override any of the following to add behavior:

- `onRightClick(ItemUseContext ctx)`
- `onUseOnBlock(ItemUseContext ctx)`
- `onUseEntity(ItemUseContext ctx)`
- `onAnvilPrepare(AnvilContext ctx)`

Each method provides full access to the context (player, item stack, block/entity, etc.) so you can do anything from teleporting to changing the world.

---

## Example UseOnBlock

```java
@Override
public void onUseOnBlock(ItemUseContext ctx) {
    ctx.player.sendMessage(Component.text("You poked a " + ctx.block().getType().name()));
    ctx.block.getWorld().strikeLightning(block.getLocation());
}
```

---

With these methods, your items can do pretty much anything you want.
