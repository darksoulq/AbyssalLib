# Creating an Item

> AbyssalLib provides an extensive Item APi for you to create your items, below you will see how to start off.

### Preparing the DeferredRegistry:
Before you create your items, you will need to create a DeferredRegistry. For simple items with only custom texture, name, and a few changed components you can just make some methods.

```Java
public class MyItems {
    public static final DeferredRegistrg<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "plugin_id");
    
    public static final Holder<Item> MY_ITEM = registerItem("my_item");
    
    private static Holder<Item> registerItem(String name) {
        ITEMS.register("name", id -> new Item(id, Material.PAPER));
    }
}
```

This will create an item with the translation key plugin_id.item.name as its name, so you must add it as an entry to your lang file.
And it will also set item model to `plugin_id:name` so make sure you have created the item definition! (if you want to set it to a vanilla item definition, see below)

To create an item with some custom data it would change to:
```Java
private static Holder<Item> registerItem(String name) {
    ITEMS.register("name", id -> {
        Item item =new Item(id, Material.PAPER);
        item.setData(new MaxStackSize(2));
        item.setData(new ItemModel(new NamespacedKey("minecraft", "stick")));
        return item;
    }
    );
}
```

Lastly you need to apply the registry in your onEnable():
```Java
public void onEnable{} {
    MyItems.ITEMS.apply();
}
```

Next section will discuss how to create items with custom behaviours!