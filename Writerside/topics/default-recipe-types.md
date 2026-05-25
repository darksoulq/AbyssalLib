# Default Recipe Types
<link-summary>Reference guide for built-in recipe types and their parameters</link-summary>

Recipe types define the structure, required ingredients, and resulting items for various crafting mechanics within the server. Below is a list of all default recipe types included and parsed by AbyssalLib.

*(Note: The `id`, `type`, and `disabled` properties are global to all recipes and are omitted from the parameter tables below).*

---

### Understanding Recipe Choices
Throughout the recipe documentation, you will see fields requiring a **Recipe Choice**.

A Recipe Choice represents the valid inputs for a *single crafting slot*. It is **always formatted as a list**, even if there is only one valid item. This allows you to easily accept multiple variations of an item (e.g., accepting either Coal or Charcoal) for the same ingredient slot.

```YAML
# Example of a Recipe Choice accepting multiple items:
input: 
  - "minecraft:coal"
  - "minecraft:charcoal"

# Example of a Recipe Choice accepting only one item (must still be a list):
input:
  - "minecraft:diamond"
```

---

### CustomShapedRecipe
**ID:** `minecraft:shaped`

Defines a standard crafting table recipe where the exact arrangement of items in the grid is strictly required.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>shape</code></td>
<td>A list of strings representing the crafting grid rows (e.g., <code>["###", " | ", " | "]</code>).</td>
</tr>
<tr>
<td><code>ingredients</code></td>
<td>A map linking the characters used in the <code>shape</code> list to a <strong>Recipe Choice</strong>.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced when crafted.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes together in the recipe book.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The recipe book category tab (e.g., <code>EQUIPMENT</code>, <code>BUILDING</code>).</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe with the same ID.</td>
</tr>
</table>

---

### CustomShapelessRecipe
**ID:** `minecraft:shapeless`

Defines a crafting table recipe where the items can be placed anywhere in the grid.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>ingredients</code></td>
<td>A <strong>list of Recipe Choices</strong>. Because a Recipe Choice is already a list, this field requires a <em>nested</em> list format. For example, <code>[["minecraft:coal", "minecraft:charcoal"], ["minecraft:stick"]]</code> requires one slot of coal/charcoal and one slot of sticks.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced when crafted.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes together in the recipe book.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The recipe book category tab.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe with the same ID.</td>
</tr>
</table>

---

### CustomFurnaceRecipe
**ID:** `minecraft:furnace`

Defines a standard smelting recipe for a Furnace block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> required to be smelted.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced when smelting is complete.</td>
</tr>
<tr>
<td><code>cooking_time</code></td>
<td>The time it takes to smelt the item, measured in ticks (20 ticks = 1 second).</td>
</tr>
<tr>
<td><code>exp</code></td>
<td>The amount of experience points dropped when taking the result out of the furnace (Float).</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes together in the recipe book.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The cooking book category tab (e.g., <code>FOOD</code>, <code>BLOCKS</code>).</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe with the same ID.</td>
</tr>
</table>

---

### CustomBlastingRecipe
**ID:** `minecraft:blasting`

Defines a smelting recipe exclusively for the Blast Furnace block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> required to be blasted.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced.</td>
</tr>
<tr>
<td><code>cooking_time</code></td>
<td>The time it takes to process the item, measured in ticks.</td>
</tr>
<tr>
<td><code>exp</code></td>
<td>The amount of experience points dropped.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The cooking book category tab.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>

---

### CustomSmokingRecipe
**ID:** `minecraft:smoking`

Defines a cooking recipe exclusively for the Smoker block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> required to be cooked.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced.</td>
</tr>
<tr>
<td><code>cooking_time</code></td>
<td>The time it takes to cook the item, measured in ticks.</td>
</tr>
<tr>
<td><code>exp</code></td>
<td>The amount of experience points dropped.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The cooking book category tab.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>

---

### CustomCampfireRecipe
**ID:** `minecraft:campfire`

Defines a cooking recipe for the Campfire block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> required to be cooked on the campfire.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack popped off the campfire when finished.</td>
</tr>
<tr>
<td><code>cooking_time</code></td>
<td>The time it takes to cook the item, measured in ticks.</td>
</tr>
<tr>
<td><code>exp</code></td>
<td>The amount of experience points dropped.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The cooking book category tab.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>

---

### CustomStonecuttingRecipe
**ID:** `minecraft:stonecutting`

Defines a conversion recipe for the Stonecutter block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> required to be placed in the stonecutter.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes together.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>

---

### CustomSmithingTransformRecipe
**ID:** `minecraft:smithing_transform`

Defines an equipment upgrade recipe for the Smithing Table block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>base</code></td>
<td>The <strong>Recipe Choice</strong> of the base equipment being upgraded (e.g., Diamond Chestplate).</td>
</tr>
<tr>
<td><code>template</code></td>
<td>The <strong>Recipe Choice</strong> of the smithing template required.</td>
</tr>
<tr>
<td><code>addition</code></td>
<td>The <strong>Recipe Choice</strong> of the material being applied (e.g., Netherite Ingot).</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced by the upgrade.</td>
</tr>
<tr>
<td><code>copy_components</code></td>
<td>(Optional) If true, preserves data components like enchantments, damage, and custom names from the base item.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>

---

### CustomPotionMix
**ID:** `minecraft:potion_mix`

Defines a custom recipe for the Brewing Stand.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> required in the bottom slots (typically a potion with specific data components).</td>
</tr>
<tr>
<td><code>ingredient</code></td>
<td>The <strong>Recipe Choice</strong> required in the top brewing slot.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack replacing the input bottles when finished.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>

---

### CustomTransmuteRecipe
**ID:** `minecraft:transmute`

A specialized crafting recipe type representing the transformation of a base item using a catalyst material.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>input</code></td>
<td>The <strong>Recipe Choice</strong> being transmuted.</td>
</tr>
<tr>
<td><code>material</code></td>
<td>The <strong>Recipe Choice</strong> of the catalyst triggering the transmutation.</td>
</tr>
<tr>
<td><code>result</code></td>
<td>The serialized item stack produced.</td>
</tr>
<tr>
<td><code>group</code></td>
<td>(Optional) A string used to group similar recipes.</td>
</tr>
<tr>
<td><code>category</code></td>
<td>(Optional) The crafting book category tab.</td>
</tr>
<tr>
<td><code>replace</code></td>
<td>(Optional) Boolean determining if this should overwrite a pre-existing recipe.</td>
</tr>
</table>