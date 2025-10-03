# Extending GuiElement and GuiLayer

> Extending GuiElement allows for creating slot elements with unique behaviour, for example an input slot which only accepts certain items.
> Whereas extending GuiLayer is useful for populating (and managing) multiple GuiElements, or Layers or anything else.

### Extending GuiElement (Example)
```Java
public class MyInputSlot implements GuiElement {
    private final Predicate<ItemStack> isAllowed;
    
    public MyInputSlot(Predicate<ItemStack> isAllowed) {
        this.isAllowed = isAllowed;
    }

    @Override
    public ItemStack render(GuiView view, int slot) {
    }

    @Override
    public ActionResult onClick(GuiView view, int slot, ClickType click, @Nullable ItemStack cursor, @Nullable ItemStack current) {
        if (!isAllowed.apply(cursor) && !cursor.isEmpty()) return ActionResult.CANCEL;
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onDrag(GuiView view, Map<Integer, ItemStack> addedItems) {
        if (!isAllowed.apply(cursor) && !cursor.isEmpty()) return ActionResult.CANCEL;
        return ActionResult.PASS;
    }

    public static MyInputSlot of(Predicate<ItemStack> isAllowed) {
        return new MyInputSlot(renderer);
    }
}
```

This creates a simple GuiElement which allows the player to put in item and take out item IF the predicate allows it.

### Extending GuiLayer (Example)
```Java
public class ListedLayers implements GuiLayer {
    private final List<GuiLayer> layers = new ArrayList<>();
    private int index = 0;
    private int lastRenderedPage = -1;

    public ListedLayers(List<GuiLayer> layers) {
        this.layers.addAll(layers);
    }

    public void next(GuiView view) {
        if (layers.isEmpty()) return;
        GuiLayer layer = layers.get(index);
        index = (index + 1) % layers.size();
        layer.cleanup(view);
    }

    public void prev(GuiView view) {
        if (layers.isEmpty()) return;
        GuiLayer layer = layers.get(index);
        index = (index - 1 + layers.size()) % layers.size();
        layer.cleanup(view);
    }

    @Override
    public void renderTo(GuiView view) {
        if (index == lastRenderedPage) return;
        GuiLayer layer = layers.get(index);
        if (layer == null) return;
        layer.renderTo(view);
        lastRenderedPage = index;
    }

    @Override
    public void cleanup(GuiView view) {
        if (!layers.isEmpty()) {
            layers.get(index).cleanup(view);
        }
    }

    public int getIndex() {
        return index;
    }
    public int getSize() {
        return layers.size();
    }
}
```

this is a layer that holds other layers and allows cycling through them.

> As you may have noticed, there is a `cleanup()` method, this is necessary so layers remove their GuiElements properly (if they have any), otherwise there would be leftover objects which could cause bugs