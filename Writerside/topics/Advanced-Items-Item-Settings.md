# Advanced Items: Item Settings

The `ItemSettings` class gives you fine-grained control over how your item behaves and looks, using Paper's DataComponent system under the hood.

You get an `ItemSettings` instance via `new ItemSettings(item)`, and you can chain methods to configure everything from durability to rarity, food, tools, and beyond.

---

## Example: Unbreakable Fire-Resistant Tool

```java
public static final RegistryObject<Item> FIRE_TOOL = ITEMS.register("fire_tool", (name, id) -> {
    Item item = new Item(id, Material.DIAMOND_PICKAXE);
    item.settings()
        .durability(500)
        .fireResistant()
        .unbreakable();
    return item;
});
```

---

## Common Settings

- `durability(int)` — sets max damage, stack size to 1, and damage to 0
- `stackSize(int)` — overrides max stack size
- `fireResistant()` — makes the item immune to fire damage
- `unbreakable()` — prevents the item from taking durability damage
- `rarity(ItemRarity)` — applies rarity tag (COMMON, UNCOMMON, RARE, EPIC)
- `useCooldown(float)` — sets use cooldown in ticks
- `usingConvertsTo(ItemStack)` — like a milk bucket turning into an empty bucket

---

## Food Items

```java
item.settings().food(
    FoodProperties.foodProperties().saturation(1).nutrition(1).build(),
    8
);
```
- `FoodProperties` - the property component of a Food, defines Nutrition and Saturation.
- `ConsumeSeconds` - the time it takes to eat the item, in seconds. (can pass in Consumable.consumable() as well).
---

## Attributes

```java
item.settings().attribute(List.of(
    new ItemSettings.Attrib(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "attack", 6.0, AttributeModifier.Operation.ADD_NUMBER))
));
```

Or per-slot:

```java
item.settings().attributeBySlot(List.of(
    new ItemSettings.AttribWithSlot(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "attack", 6.0, AttributeModifier.Operation.ADD_NUMBER), EquipmentSlotGroup.MAINHAND)
));
```

---

## Weapon Items

```java
item.settings().weapon(7, 1);
```

- `itemDamagePerAttack`: how much damage it deals
- `disableBlockingForSeconds`: disables shields for this many seconds

---

You can chain as many settings as you want.
