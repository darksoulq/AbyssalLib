# Extending the item class

> For more complex cases, for example when you want to add behaviour for when a player breaks a block with the item you have to extend the item class, which is done as shown below.

### Extending the Item class:
```Java
public class MyItem extends Item {
    public MyItem(Identifier id) {
        super(id, Material.PAPER);
        createTooltip(tooltip);
        updateTooltip();
    }
    
    @Override
    public void createTooltip(Tooltip tooltip) {
        tooltip.lines.clear();
        tooltip.addLine(Component.text("Sends a message whenever a block is broken!"));
    }
    
    @Override
    public ActionResult postMine(LivingEntity source, Block target) {
        source.sendMessage(Component.text("You broke a Block!"));
        return ActionResult.PASS;
    }
}
```

> ActionResult.PASS makes it so the event continues to process, ActionResult.CANCEL cancels the event

then you have to register it like this:
```Java
ITEMS.register("my_item", MyItem::new);
```

There are other methods that you can override:

<list>
<li>
<code>ActionResult postHit(LivingEntity source, Entity target</code>
</li>
<li>
<code>ActionResult onUseOn(UseContext ctx)</code>
</li>
<li>
<code>void onUse(LivingEntity source, EquipmentSlot hand)</code>
</li>
<li>
<code>void onCraftedBy(Player player)</code>
</li>
</list>