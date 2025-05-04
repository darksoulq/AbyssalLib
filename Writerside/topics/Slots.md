# Slots

Slots allow for alot of custom behaviours for your guis, as evident from StaticSlot and ButtonSlot, now we will see how to make our own slots.

## Step 1: Make a class extending Slot

```Java
public class MySlot extends Slot {
    private final ItemStack item;

    public MySlot(int index, ItemStack item) {
        super(index);
        this.item = item;
    }

    @Override
    public ItemStack item() {
        return item;
    }

    @Override
    public void item(ItemStack item) {

    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onClick(GuiClickContext ctx) {

    }

    @Override
    public void onDrag(GuiDragContext ctx) {

    }

    @Override
    public void onTick(AbstractGui gui) {

    }
}
```

- This example is what StaticSlot is, however, you can add any logic whatsover using all the overridable methods, click behavious, drag behaviours, tick changes (for animating perhaps)

Now you can add this slot to your guis!

### Example: AnimatedSlot

This is how the AnimatedSlot is coded:

```Java
public class AnimatedSlot extends Slot {
    private final Supplier<ItemStack> frameSupplier;

    public AnimatedSlot(int index, Supplier<ItemStack> frameSupplier) {
        super(index);
        this.frameSupplier = frameSupplier;
    }

    @Override
    public ItemStack item() {
        return frameSupplier.get();
    }

    @Override
    public void item(ItemStack item) {

    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onClick(GuiClickContext ctx) {}
    @Override
    public void onDrag(GuiDragContext ctx) {}
    @Override
    public void onTick(AbstractGui gui) {
        gui.inventory().setItem(index, item());
    }
}
```