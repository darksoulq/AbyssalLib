# Custom GuiElements
<link-summary>Guide to creating custom interactive GUI elements</link-summary>

If the built-in GUI components do not meet your requirements, you can create custom elements by implementing the `GuiElement` interface. Elements manage their own visual state and handle raw interaction events.

### The GuiElement Interface
The `GuiElement` interface provides three primary methods you need to implement or override.

<note>
Always use the methods that accept <code>GuiClickContext</code> and <code>GuiDragContext</code>. The legacy methods accepting raw parameters are deprecated and slated for removal.
</note>

<table>
<tr>
<th>Method</th>
<th>Information</th>
</tr>
<tr>
<td><code>render(GuiView, int)</code></td>
<td>Determines the <code>ItemStack</code> to display in the assigned slot. Return <code>null</code> for an empty slot.</td>
</tr>
<tr>
<td><code>onClick(GuiClickContext)</code></td>
<td>Fires when the element is clicked. Returns an <code>ActionResult</code> (e.g., <code>PASS</code> or <code>CANCEL</code>) to control the underlying Bukkit event.</td>
</tr>
<tr>
<td><code>onDrag(GuiDragContext)</code></td>
<td>Fires when an item is dragged across the element's slot. Returns an <code>ActionResult</code>.</td>
</tr>
</table>

### Implementing a Custom Element
For this example, we will create a `CounterElement`. This element will display a diamond, increment its stack size on left-click, and decrement it on right-click.

```Java
public class CounterElement implements GuiElement {
    
    private int count;

    public CounterElement(int initialCount) {
        this.count = Math.max(1, Math.min(64, initialCount));
    }

    @Override
    public @Nullable ItemStack render(GuiView view, int slot) {
        ItemStack item = new ItemStack(Material.DIAMOND, count);
        item.setData(DataComponentTypes.ITEM_NAME, Component.text("Current Count: " + count));
        return item;
    }

    @Override
    public ActionResult onClick(GuiClickContext ctx) {
        // Adjust the count based on the click type
        if (ctx.clickType().isLeftClick()) {
            count = Math.min(64, count + 1);
        } else if (ctx.clickType().isRightClick()) {
            count = Math.max(1, count - 1);
        }
        
        // Prevent the player from picking up the item
        return ActionResult.CANCEL;
    }

    @Override
    public ActionResult onDrag(GuiDragContext ctx) {
        // Prevent items from being dragged into this slot
        return ActionResult.CANCEL;
    }
}
```

### Applying the Custom Element
Once the class is created, you can apply it to your GUI just like any built-in element using `builder.set()`.

```Java
public final class TestGUI {
    
    public static void open(Player player) {
        Gui.Builder builder = Gui.builder(MenuType.GENERIC_9X3, Component.text("Custom Element GUI"));
        
        // Place our custom CounterElement in the middle slot (index 13) starting at count 1
        builder.set(SlotPosition.top(13), new CounterElement(1));
        
        GuiManager.open(player, builder.build());
    }
}
```