# Tags

Tags allow you to group items under a shared label (e.g. "axes", "pickaxes"), useful for logic like tool compatibility, loot rules, or filtering in custom systems.

AbyssalLib provides an in-code Tag API, meaning you don’t need to write or load any JSON.

## Step 1: Create and Register a Tag

To define your own item tag, use a `DeferredRegistry<ItemTag>` just like you would for blocks or items. (BlockTag for block tags).

```Java
public static final DeferredRegistry<ItemTag> TAGS = DeferredRegistry.create(BuiltinRegistries.ITEM_TAGS, MODID);

public static final DeferredObject<ItemTag> HAMMERS = TAGS.register("hammers", (name, id) -> new ItemTag(name));
```

This creates a tag called hammers with the ID `modid:hammers`.

## Step 2: Add Items to the Tag
You can add items to your tag in two ways:
```Java
HAMMERS.get().add(MY_HAMMER.get());
```
Option 2: Lookup by ID
```Java
BuiltinRegistries.ITEM_TAGS.get(new ResourceLocation("modid", "hammers")).add(MY_HAMMER.get());
```
Use whichever form is more convenient — both do the same thing.

## Step 3: Check if an Item Has a Tag
To check if an item is in a tag, use the `.hasTag()` method:
```Java
if (item.hasTag(new ResourceLocation("modid", "hammers"))) {
    // do something if it's a hammer
}
```
This works with any `Item` instance and will return true if the item belongs to the tag.

## Built-in Tags
AbyssalLib includes some built-in tags you can use out of the box:
- BuiltinTags.AXES (`"abyssallib:axes"`)
- BuiltinTags.PICKAXES (`"abyssallib:pickaxes"`)
- BuiltinTags.SHOVELS (`"abyssallib:shovels"`)
- BuiltinTags.HOES (`"abyssallib:hoes"`)
- BuiltinTags.SWORDS (`"abyssallib:swords"`)
