# LootTables

Loot tables in AbyssalLib allow you to define complex, randomized item drops in code. These can be used in blocks, entities, containers, or anywhere else you want controlled loot logic.

You define loot tables using a clean, chainable API — no JSON required.

## Step 1: Create a Loot Table
You can construct a `LootTable` by adding one or more `LootPools`. Each pool can contain multiple entries, modifiers, and conditions.
```Java
LootTable table = new LootTable()
    .addPool(new LootPool(3) // Rolls this pool 3 times
        .addEntry(new ItemLootEntry(() -> new ItemStack(Material.GOLD_INGOT))
            .apply(new SetCountFunction(1, 5))) // Drop 1–5 gold ingots
        .addEntry(new ItemLootEntry(() -> new ItemStack(Material.IRON_INGOT))) // Drop 1 iron ingot
        .when(new RandomChanceCondition(0.75f)) // Only run this pool 75% of the time
    );
```

You can add multiple pools with different rolls, items, and conditions to build advanced loot logic.

## Step 2: Generate Loot
To actually get the drops, use .generate() with a LootContext:
```Java
List<ItemStack> loot = table.generate(new LootContext());
```
You can then drop or give the items however you want:
```Java
for (ItemStack item : loot) {
    player.getWorld().dropItemNaturally(location, item);
}
```

## Step 3: Register a Loot Table (Optional)
If you want to register your table for reuse (or for access for other plugins), use a `DeferredRegistry<LootTable>`:
```Java
public static final DeferredRegistry<LootTable> LOOT_TABLES = DeferredRegistry.create(BuiltinRegistries.LOOT_TABLES, MODID);

public static final DeferredObject<LootTable> CUSTOM_LOOT = LOOT_TABLES.register("custom_loot", (name, id) ->
    new LootTable()
        .addPool(new LootPool(1)
            .addEntry(new ItemLootEntry(() -> new ItemStack(Material.DIAMOND)))
        )
);
```
Then just call `CUSTOM_LOOT.get().generate(ctx)` anywhere you need the drops.

## Loot Modifiers
Each LootEntry can be modified with functions:
- `SetCountFunction(int min, int max)` – Randomized stack size
- (More can be made by implementing `LootFunction`)
You can chain `.apply()` multiple times if needed.

## Conditions
Loot pools can be wrapped in conditions like:
- `RandomChanceCondition(float chance)` – Only include the pool if a random roll succeeds