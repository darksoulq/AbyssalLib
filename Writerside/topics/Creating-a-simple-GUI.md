# Creating a simple GUI

> AbyssalLib comes with a Builder-Style (or optionally extendable) GUI Api which is made to be easy to use.

### Setting up the GUI
```Java
public class MyGuiClass {
    public static Gui create() {
        return new Gui.Builder(MenuType.GENERIC_9x6, Component.text("title"))
                .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
                .set(SlotPosition.top(3), GuiButton.of(ItemStack, (view, clickType) -> {
                    // Logic
                }))
                .set(SlotPosition.top(7), GuiButton.of(ItemStack, (view, clickType))
                .build();
    }
}
```

Afterwards open it any time like so:
```Java
GuiManager.open(Player, MyGuiClass.create());
```

> If you wish for a shared GUI, simply dont call .create() each time, instead store it as a variable and pass that in.

### Adding a Paginated Elements Layer:
```Java
public static Gui create() {
    int[] slots = {
            1, 2, 4, 5, 6, 8, 9
    };
    PaginatedElements elems = new PaginatedElements(elementsList, slots, GuiView.Segment.TOP);
        
    return new Gui.Builder(MenuType.GENERIC_9x6, Component.text("title"))
            .addFlags(GuiFlag.DISABLE_ADVANCEMENTS, GuiFlag.DISABLE_ITEM_PICKUP)
            .addLayer(elems)
            .set(SlotPosition.top(3), GuiButton.of(ItemStack, (view, clickType) -> {
                // Logic
            }))
            .set(SlotPosition.top(7), GuiButton.of(ItemStack, (view, clickType))
            .build();
}
```

> - The First argument is a List&lt;GuiElement&gt; (the elements to be populated)
> - The Second argument is the slot arrays, that is the slots used for this pagination
> - The Third Argument is the Segment that this should apply to (TOP inv or BOTTOM inv)
> - This does NOT come with premade next/previous buttons, you must use .set to create Gui Button that call elems.next() and elems.prev().

---

You can also extend AbstractGui in case you dont want to use builder.
```Java
public class MyGui extends AbstractGui {
    public MyGui() {
        super(MenuType.FURNACE, Component.text("title"));
    }
    
    @Override
    protected void init() {
        /*
                set(..)
                addFlag(..)
                etc
         */
    }
    
    @Override
    protected void onOpen() {}
    
    @Override
    protected void onClose() {}
}
```
