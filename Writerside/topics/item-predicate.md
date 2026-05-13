---
switcher-label: In
---

# Item Predicates
<link-summary>Guide to creating and utilizing item filters</link-summary>

An `ItemPredicate` is a filter used to evaluate `ItemStack` instances. Instead of manually checking item types and component data, Item Predicates allow you to construct rules that check an item's identity, the presence or absence of data components, exact component values, and nested sub-predicates.

Because Item Predicates support the `Condition` logical tree, you can natively implement `AND` (`all_of`) and `OR` (`any_of`) gates into your checks.

### Creating a Predicate {switcher-key="Code"}
To construct an Item Predicate in Java, use `ItemPredicate.builder()`.

The builder provides a fluent API to define your requirements. You can evaluate the predicate at any time by passing an `ItemStack` into its `test()` method.

```Java
public final class PredicateExample {

    public static final ItemPredicate PRISTINE_SWORD = ItemPredicate.builder()
        // 1. Must be a Diamond Sword
        .material(Material.DIAMOND_SWORD)
        
        // 2. Must have Custom Model Data applied
        .with(Key.key("minecraft:custom_model_data"))
        
        // 3. Must NOT have the Unbreakable component
        .without(Key.key("minecraft:unbreakable"))
        
        // 4. Must have EXACTLY 0 damage
        .value(new Durability(0))
        
        // 5. Logical OR: Must have either enchantments or stored enchantments
        .withAny(Key.key("minecraft:enchantments"), Key.key("minecraft:stored_enchantments"))
        
        .build();

    public static void checkItem(Player player, ItemStack item) {
        if (PRISTINE_SWORD.test(item)) {
            player.sendMessage("Item matches the predicate.");
        }
    }
}
```

#### Builder Methods
<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>id(Key)</code> / <code>material(Material)</code></td>
<td>Mandates that the item matches a specific base Key or vanilla Material.</td>
</tr>
<tr>
<td><code>with(Key)</code></td>
<td>Requires the presence of a specific component Key on the item.</td>
</tr>
<tr>
<td><code>without(Key)</code></td>
<td>Requires the absence of a specific component Key on the item.</td>
</tr>
<tr>
<td><code>value(DataComponent&lt;?&gt;)</code></td>
<td>Requires an exact value match against the provided data component.</td>
</tr>
<tr>
<td><code>check(ItemPredicate)</code></td>
<td>Evaluates an entire nested sub-predicate against the item.</td>
</tr>
<tr>
<td><code>withAny(...)</code> / <code>valueAny(...)</code></td>
<td>Logical <code>OR</code>. Requires the item to satisfy at least one of the provided rules.</td>
</tr>
</table>

---

### Creating a Predicate {switcher-key="YAML" id="creating-a-predicate_1"}
Predicates are inherently data-driven and can be loaded directly from configuration files.

To create a predicate using YAML, create a file at <path>plugins/AbyssalLib/predicates/item/&lt;namespace&gt;/&lt;id&gt;.yml</path>. For this example, we will recreate the "Pristine Sword" predicate at <path>abyssallib_example/pristine_sword.yml</path>.

```YAML
# 1. Must be a Diamond Sword
id: "minecraft:diamond_sword"

# 2. Must have Custom Model Data applied, AND
# 5. Logical OR: Must have either enchantments or stored enchantments
with:
  - "minecraft:custom_model_data"
  - any_of:
      - "minecraft:enchantments"
      - "minecraft:stored_enchantments"

# 3. Must NOT have the Unbreakable component
without:
  - "minecraft:unbreakable"

# 4. Must have EXACTLY 0 damage
components:
  - "minecraft:damage": 0
```

#### Utilizing Loaded Predicates
During server startup, AbyssalLib automatically parses YAML definitions in the predicates folder and registers them under the `Registries.ITEM_PREDICATES` registry.

You can retrieve and use the predicate in your code using its namespaced key:

```Java
public void checkLoadedItem(ItemStack item) {
    // Fetch the predicate using the namespace (folder name) and filename
    ItemPredicate predicate = Registries.ITEM_PREDICATES.get("abyssallib_example:pristine_sword");
    
    if (predicate != null && predicate.test(item)) {
        // The item matches the YAML definition.
    }
}
```

<tip>
Because Item Predicates are registered, you can reference an existing predicate directly inside <em>another</em> predicate simply by providing its string ID instead of rewriting the full object definition.
</tip>