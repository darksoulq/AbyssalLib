# Custom GuiLayers
<link-summary>Guide to creating custom GUI layers</link-summary>

While `GuiElement`s handle individual interactive slots, `GuiLayer`s are used to manage multi-slot visuals, background patterns, or dynamic area effects (like borders or paginated data) over a GUI inventory.

### The GuiLayer Interface
To create a custom layer, implement the `GuiLayer` interface. It provides two methods to handle the lifecycle of the visual components.

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>renderTo(GuiView)</code></td>
<td>Applies the layer's logic to the active view. This usually involves injecting <code>GuiElement</code>s into the main GUI element map.</td>
</tr>
<tr>
<td><code>cleanup(GuiView)</code></td>
<td>Fires when the layer is removed or overwritten. You must remove your injected elements and clear the physical inventory slots.</td>
</tr>
</table>

### Implementing a Custom Layer
For this example, we will create an `OutlineLayer`. This layer calculates the perimeter of the target GUI and fills the border slots with a specified background item.

```Java
public class OutlineLayer implements GuiLayer {
    
    private final GuiElement fillElement;

    public OutlineLayer(ItemStack fillItem) {
        this.fillElement = GuiItem.of(fillItem);
    }

    @Override
    public void renderTo(GuiView view) {
        Gui gui = view.getGui();
        int size = view.getTop().getSize();

        for (int i = 0; i < size; i++) {
            // Check if the current slot sits on the outer border of the inventory
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                // Inject the element into the GUI's element map
                gui.getElements().put(SlotPosition.top(i), fillElement);
            }
        }
    }

    @Override
    public void cleanup(GuiView view) {
        Gui gui = view.getGui();
        Inventory top = view.getTop();
        int size = top.getSize();

        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                // Remove the element from the map and clear the physical item
                gui.getElements().remove(SlotPosition.top(i));
                top.setItem(i, null);
            }
        }
    }
}
```

### Applying the Custom Layer
Once the layer class is created, apply it to your GUI using `builder.addLayer()`.

```Java
public final class TestGUI {
    
    public static void open(Player player) {
        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X6, TextUtil.parse("<green>Outlined GUI</green>"));
        
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        glass.setData(DataComponentTypes.ITEM_NAME, Component.empty());
        
        // Add our custom layer to the GUI
        builder.addLayer(new OutlineLayer(glass));
        
        GuiManager.open(player, builder.build());
    }
}
```

<note>
Layers are processed in the order they are added. If elements share the same slot position, the layer added last will overwrite the previous ones.
</note>