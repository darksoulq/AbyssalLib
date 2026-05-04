# Creating Your First Placeholder
<link-summary>Guide to creating and registering strongly-typed custom placeholders</link-summary>

Unlike standard string-based placeholder APIs, AbyssalLib placeholders are strongly typed (`Placeholder<T>`). This allows you to resolve complex objects and use the `format(T value)` method to dictate exactly how they are styled into text.

### Creating a custom placeholder
To create a placeholder, extend the `Placeholder<T>` class, where `T` is the specific object type you want to return.

By default, the API will convert unknown objects to strings. However, by overriding `format(T value)`, you can return a rich MiniMessage `Component`.

For this example, we will create a placeholder that finds the `EquipmentSlot` of the player's most damaged armor piece and formats it nicely.

```Java
public class DamagedSlotPlaceholder extends Placeholder<EquipmentSlot> {

    public DamagedSlotPlaceholder(Key id) {
        // Define the ID and the specific class type this placeholder returns
        super(id, EquipmentSlot.class);
    }

    @Override
    public PlaceholderResult<EquipmentSlot> resolve(PlaceholderContext context) {
        Player player = context.getPlayer();
        if (player == null) return PlaceholderResult.empty();

        EquipmentSlot mostDamaged = null;
        double maxDamagePercent = -1;

        // Iterate through the player's armor to find the most damaged piece
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) continue;

            ItemStack item = player.getInventory().getItem(slot);
            if (item == null || !item.hasItemMeta() || !(item.getItemMeta() instanceof Damageable meta)) continue;

            double damagePercent = (double) meta.getDamage() / item.getType().getMaxDurability();
            if (damagePercent > maxDamagePercent) {
                maxDamagePercent = damagePercent;
                mostDamaged = slot;
            }
        }

        // Return empty if no damaged armor is found, otherwise return the slot
        if (mostDamaged == null) return PlaceholderResult.empty();
        return PlaceholderResult.success(mostDamaged);
    }

    // Override the format method to define the visual text output
    @Override
    public Component format(EquipmentSlot value) {
        // Convert "CHEST" to "Chest" and apply a red color
        String niceName = value.name().substring(0, 1).toUpperCase() + value.name().substring(1).toLowerCase();
        return TextUtil.parse("<red>" + niceName + "</red>");
    }
}
```

<note>
Overriding <code>format()</code> is highly recommended when returning enums or custom data objects, as it gives you full control over capitalization, colors, and translatable components without cluttering your <code>resolve()</code> logic.
</note>

### Using Placeholder Arguments
Placeholders can accept dynamic arguments defined inside the MiniMessage placeholder tag (e.g., `<placeholder:abyssallib_example:slot_by_name:head>`). You can access these arguments through the `PlaceholderContext`.

```Java
public class SlotByNamePlaceholder extends Placeholder<EquipmentSlot> {

    public SlotByNamePlaceholder(Key id) {
        super(id, EquipmentSlot.class);
    }

    @Override
    public PlaceholderResult<EquipmentSlot> resolve(PlaceholderContext context) {
        // Check if an argument was provided
        if (!context.hasArgs()) return PlaceholderResult.error("Missing slot argument");

        // Fetch the first argument as a String
        String slotName = context.getRaw(0, "").toUpperCase(Locale.ROOT);

        try {
            EquipmentSlot slot = EquipmentSlot.valueOf(slotName);
            return PlaceholderResult.success(slot);
        } catch (IllegalArgumentException e) {
            return PlaceholderResult.error("Invalid slot name: " + slotName);
        }
    }

    @Override
    public Component format(EquipmentSlot value) {
        return TextUtil.parse("<yellow>" + value.name() + "</yellow>");
    }
}
```

### Registering the Placeholder
Placeholders must be registered using a `DeferredRegistry` targeting `Registries.PLACEHOLDERS`. The namespace and ID you provide here will dictate the base tag used in-game.

```Java
public final class CustomPlaceholders {

    public static final DeferredRegistry<Placeholder<?>> PLACEHOLDERS = DeferredRegistry.create(Registries.PLACEHOLDERS, AbyssalLibExample.PLUGIN_ID);

    // This will be accessible in-game as <placeholder:abyssallib_example:damaged_slot>
    public static final Placeholder<?> DAMAGED_SLOT = PLACEHOLDERS.register("damaged_slot", id -> new DamagedSlotPlaceholder(id));

    // This will be accessible in-game as <placeholder:abyssallib_example:slot_by_name:<argument>>
    public static final Placeholder<?> SLOT_BY_NAME = PLACEHOLDERS.register("slot_by_name", id -> new SlotByNamePlaceholder(id));
}
```

<note>
Do not forget to call <code>CustomPlaceholders.PLACEHOLDERS.apply()</code> in your plugin's <code>onEnable()</code> method.
</note>