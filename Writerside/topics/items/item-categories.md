# Custom Item Categories
<link-summary>Guide to organizing your items into distinct categories within the Item Menu GUI</link-summary>

Item Categories (`ItemCategory`) are the interactive buttons that appear within the main Item Menu. By default, when no category is explicitly created, an **All** category is automatically generated containing every item registered by your plugin.

However, as your plugin grows, using Item Categories allows you to neatly organize items (e.g., Weapons, Magic, Materials) as well as hide specific items you don't want visible in the public Item Menu.

### Creating an ItemCategory
To begin, you will need to create a `DeferredRegistry` for the `ItemCategory` and then use the provided builder to construct the category itself.

<tip>
The order in which you create your categories, AND the order of the inputs in the <code>.add()</code> method, directly dictates how they are sorted and displayed within the GUI!
</tip>

```Java
public final class ItemCategories {
    public static final DeferredRegistry<ItemCategory> CATEGORIES = DeferredRegistry.create(Registries.ITEM_CATEGORIES, AbyssalLibExample.PLUGIN_ID);
    
    // Create an "Example Items" category
    public static final ItemCategory EXAMPLE_ITEMS = CATEGORIES.register("example_items", id -> ItemCategory.builder(id)
        .icon(Items.RUBY) // The item that represents this category in the menu
        .add(Items.RUBY, Items.FIRE_SWORD, Items.EDIBLE_PAPER) // The items contained inside
        .build());
}
```

<warning>
The <code>.icon()</code> MUST be set, and there MUST be at least 1 item added to the category. If a category is empty or missing an icon, the plugin will disable with an error on startup.
</warning>

Just like with items, call `ItemCategories.CATEGORIES.apply()` inside your `onEnable()` to register them, and launch the server.

<img src="item_category_1.png" alt="Category without translation"/>

You will notice that the category title currently displays an untranslated key. To fix this, we will head over to our previously created `Pack` class (from the <a href="first-item.md">Create Your First Item</a> guide) and add the necessary translation strings.

```Java
enUS.put("category.item.abyssallib_example.example_items", "Example Items");

// We will also add translation for the main plugin title to fix the top of the GUI.
enUS.put("plugin.abyssallib_example", "AbyssalLib Example");
```

Now, when you regenerate the pack and load into the server, you will see the correctly formatted title for the category and the menu.

<img src="item_category_2.png" alt="Category with translation"/>