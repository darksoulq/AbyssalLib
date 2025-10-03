# Creating a Tag

> Tags allow grouping items and blocks typically for use in conditions.
> AbyssalLib comes with 2 default tag types which you can base yours upon, `ItemTag` and `BlockTag`.

### Creating an ItemTag
```Java
public class MyTags {
    public static final DeferredRegistry<Tag<?>> TAGS = DeferredRegistry.create(Registries.TAGS, "plugin_id");
    
    public static final Holder<Tag<?>> MY_TAG = TAGS.register("my_tag", ItemTag::new);
}
```

Then apply the registry inside your `onEnable()`.

### Creating a BlockTag
```Java
public class MyTags {
    public static final DeferredRegistry<Tag<?>> TAGS = DeferredRegistry.create(Registries.TAGS, "plugin_id");

    public static final Holder<Tag<?>> MY_BLOCK_TAG = TAGS.register("my_tag", BlockTag::new);
}
```

### Using Tags
Adding an item/block to the tag:
```Java
(ItemTag MyTags.MY_TAG).add(ItemStack);
(BlockTag MyTags.MY_BLOCK_TAG).add(BridgeBlock<?>);
```

Checking if an item/block has a given Tag:
```Java
(ItemTag MyTags.MY_TAG).contains(ItemStack);
(BlockTag MyTags.MY_BLOCK_TAG).contains(BridgeBlock<?>);
```

You can also make your own Tags by extending `Tag<>` class and implementing all the methods and then using that instead of ItemTag and BlockTag.
