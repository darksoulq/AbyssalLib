# Making Paged GUIs
<link-summary>Guide to creating paginated menus using PagedLayer</link-summary>

This page covers the usage of `PagedLayer<T>` inside GUIs. `PagedLayer<T>` allows for pagination across a specific set of slots, rather than forcing the entire GUI to act as a single paginated menu.

### Preparing the data and slots
To create a paginated layer, you need a list of data (your type `T`) and an array of slot indices where the elements will be displayed.

You can use `SlotUtil.grid()` to easily generate an array of slots representing a specific rectangular area. For this example, we will paginate a list of all Bukkit `Material` types.

```Java
// 1. Prepare the data list
List<Material> materials = List.of(Material.values()).stream()
    .filter(Material::isItem)
    .sorted(Comparator.comparing(Enum::name))
    .toList();

// 2. Generate a grid of slots in the TOP segment
int[] slots = SlotUtil.grid(GuiView.Segment.TOP, 0, 5, 9, 6, 9)
    .stream()
    .mapToInt(SlotPosition::index)
    .toArray();
```

### Constructing the PagedLayer
Use `PagedLayer.of()` to construct the layer. You must provide the data list, the slot array, the target inventory segment, and a mapping function.

The mapping function takes your data item and its index, and must return a `GuiElement` (such as a `GuiItem`) to display in the slot.

```Java
PagedLayer<Material> pages = PagedLayer.of(
    materials,
    slots,
    GuiView.Segment.TOP,
    (mat, index) -> {
        ItemStack item = new ItemStack(mat);

        item.setData(DataComponentTypes.ITEM_NAME, Component.text(mat.name().toLowerCase()));
        item.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
            Component.text("Index: " + index),
            Component.text("Type: " + mat.name())
        )));

        return GuiItem.of(item);
    }
);
```

### Adding navigation and applying the layer
To allow players to change pages, add standard `GuiButton` elements outside of your defined grid slots. These buttons will call the `next()` and `previous()` methods on your `PagedLayer` instance.

Finally, apply the layer to your `Gui.Builder`.

```Java
public static void openMaterialBrowser(Player viewer) {
    Gui.Builder builder = Gui.builder(
        MenuType.GENERIC_9X6,
        TextUtil.parse("<green>Material Browser</green>")
    );

    // ... (insert data, slot, and PagedLayer construction from above) ...

    builder.addLayer(pages);

    // Navigation buttons
    builder.set(SlotPosition.top(53),
        GuiButton.of(Items.FORWARD.getStack(), ctx -> pages.next(ctx.view()))
    );

    builder.set(SlotPosition.top(45),
        GuiButton.of(Items.BACKWARD.getStack(), ctx -> pages.previous(ctx.view()))
    );

    GuiManager.open(viewer, builder.build());
}
```

<note>
Because <code>PagedLayer</code> only targets the specific slots you provide, you can freely place static items, borders, or other interactive elements in the remaining slots of the GUI using <code>builder.set()</code>.
</note>

Now, when you open the GUI in-game, you will see a fully functional, paginated grid.

<img src="gui_3.png" alt="Paged GUI Showcase" style="block"/>