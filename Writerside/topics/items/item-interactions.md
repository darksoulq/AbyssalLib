# Custom Item Interactions
<link-summary>Guide to adding custom behaviour on items using overridable event methods</link-summary>

This page covers how to breathe life into your custom items. While basic items are great for simple resources or food, you will often want your items to interact dynamically with the world—like a sword that sets enemies on fire, or a tool that triggers magic spells.

To achieve this, you must extend the base `Item` class and override its provided event methods.

<tip>
Because these methods are built directly into the <code>Item</code> class, you do not need to register separate Bukkit Event Listeners. AbyssalLib handles the event routing to the specific item for you!
</tip>

<deflist collapsible="true">
<def title="ActionResult" default-state="expanded">
<code>ActionResult</code> decides whether or not the underlying server event should be cancelled; it can be used to prevent vanilla damage, stop block breaking, and more. <br/>
<code>ActionResult.PASS</code> -> Allows the event to proceed un-interrupted. <br/>
<code>ActionResult.CANCEL</code> -> Cancels the event behind the scenes (<emphasis><code>event.setCancelled(true)</code></emphasis>).
</def>
</deflist>

### Overridable Events
The `Item` class provides a wide array of overridable events so you can add precise functionality to your items.

<table>
<tr>
<th>Method</th>
<th>Information</th>
<th>Return</th>
</tr>
<tr>
<td><code>onMine</code></td>
<td>Called when the player mines a block.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onHit</code></td>
<td>Called when the player hits an entity.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onUseOn</code></td>
<td>Called when the player <shortcut>Right-Click</shortcut>s a block or an entity.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onUse</code></td>
<td>Called when the player uses an item (<shortcut>Right-Click</shortcut>s air, eats food, etc).</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onInventoryTick</code></td>
<td>Called every tick while the item is inside the player's inventory.</td>
<td><code>void</code></td>
</tr>
<tr>
<td><code>onSlotChange</code></td>
<td>Called when the item's position within the player's inventory changes.</td>
<td><code>void</code></td>
</tr>
<tr>
<td><code>onClick</code></td>
<td>Called when the player clicks the item inside an inventory GUI.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onDrop</code></td>
<td>Called when the player drops the item.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onPickup</code></td>
<td>Called when the player picks up the item.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onSwapHand</code></td>
<td>Called when the player swaps the item from/into their offhand.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onAnvil</code></td>
<td>Called when the item is placed inside an anvil.</td>
<td><code>ActionResult</code></td>
</tr>
<tr>
<td><code>onCraft</code></td>
<td>Called when a player crafts this item.</td>
<td><code>void</code></td>
</tr>
</table>

### Example: The `onHit` event
Let's say you want to make a specialized weapon that sets the target on fire when hit. Instead of registering it inline, you would create a custom `Item` class.

First, we set up the item's base stats in the constructor:

```Java
public final class FireSword extends Item {
    public FireSword(Key id) {
        super(id);
        // Set texture, durability, and damage
        setData(new ItemModel(NamespacedKey.minecraft("iron_sword")));
        setData(new MaxDurability(120));
        setData(new WeaponComponent(Weapon.weapon()
            .itemDamagePerAttack(3)
            .build()));
        
        createTooltip(tooltip);
        updateTooltip();
    }

    // Next, we will override onHit here
}
```

Then, we override the `onHit` event inside that same class and inflict fire on the `target` entity:

```Java
    @Override
    public ActionResult onHit(LivingEntity source, Entity target) {
        // Set target on fire for 3 seconds (20 ticks per second)
        target.setFireTicks(20 * 3);
        
        // We pass the event so normal weapon damage still applies
        return ActionResult.PASS;
    }
```

<note>
If we returned <code>ActionResult.CANCEL</code> here, the target would catch on fire, but they would take 0 initial damage from the sword swing itself!
</note>

As usual, register the item in your main registry class and launch the server. As you can see, hitting an entity successfully sets it ablaze.

<video src="using_fire_sword.mp4" preview-src="using_fire_sword.png"/>