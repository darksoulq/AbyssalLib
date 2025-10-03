# Adding Recipes
> Recipes use .yml format!
> 
For adding recipes navigate to `/plugin/AbyssalLib/recipes/` first then follow the below steps:

- Create your file, it can be in a subfolder as well.
- Now depending on which recipe you wish to make, use the respective template:

<tabs>
<tab title="Shaped">
<code-block lang="YAML">
type: minecraft:shaped
id: mycontent:myrecipe
shape:
  - "x x"
  - "zyx"
  - "xyz"
ingredients:
  x:
    - "minecraft:apple"
  y:
    - "abyssallib:someplugin:some_item"
  z:
    - "minecraft:iron_ingot"
result: "minecraft:diamond"
# Optional params
# group: "mygroup"
# category: "BUILDING" # OR "EQUIPMENT", "MISC", "REDSTONE"
</code-block>
</tab>
<tab title="Shapeless">
<code-block lang="YAML">
type: minecraft:shapeless
id: mycontent:myrecipe
ingredients:
  - "minecraft:apple"
  - "abyssallib:someplugin:some_item"
  - "minecraft:iron_ingot"
result: "minecraft:diamond"
# Optional params
# group: "mygroup"
# category: "BUILDING" # OR "EQUIPMENT", "MISC", "REDSTONE"
</code-block>
</tab>
<tab title="Transmute">
<code-block lang="YAML">
type: minecraft:transmute
id: mycontent:myrecipe
input: "minecraft:shears"
material: "minecraft:stick"
result: "minecraft:iron_sword"
# Optional params
# group: "mygroup"
# category: "BUILDING" # OR "EQUIPMENT", "MISC", "REDSTONE"
</code-block>
</tab>
<tab title="Furnace">
<code-block lang="YAML">
type: minecraft:furnace
id: mycontent:myrecipe
input: "minecraft:bread"
result: "minecraft:iron_sword"
cooking_time: 20 # in ticks
exp: 3
# Optional params
# group: "mygroup"
# category: "BLOCKS" # OR "FOOD", "MISC".
</code-block>
</tab>
<tab title="Smoking">
<code-block lang="YAML">
type: minecraft:smoking
id: mycontent:myrecipe
input: "minecraft:bread"
result: "minecraft:iron_sword"
cooking_time: 20 # in ticks
exp: 3
# Optional params
# group: "mygroup"
# category: "BLOCKS" # OR "FOOD", "MISC".
</code-block>
</tab>
<tab title="Blasting">
<code-block lang="YAML">
type: minecraft:blasting
id: mycontent:myrecipe
input: "minecraft:bread"
result: "minecraft:iron_sword"
cooking_time: 20 # in ticks
exp: 3
# Optional params
# group: "mygroup"
# category: "BLOCKS" # OR "FOOD", "MISC".
</code-block>
</tab>
<tab title="Campfire">
<code-block lang="YAML">
type: minecraft:campfire
id: mycontent:myrecipe
input: "minecraft:bread"
result: "minecraft:iron_sword"
cooking_time: 20 # in ticks
exp: 3
# Optional params
# group: "mygroup"
# category: "BLOCKS" # OR "FOOD", "MISC".
</code-block>
</tab>
<tab title="Stonecutting">
<code-block lang="YAML">
type: minecraft:stone_cutting
id: mycontent:myrecipe
input: "minecraft:bread"
result: "minecraft:iron_sword"
# Optional params
# group: "mygroup"
</code-block>
</tab>
<tab title="Smithing Transform">
<code-block lang="YAML">
type: minecraft:smithing_transform
id: mycontent:myrecipe
base: "minecraft:bread"
base: "minecraft:bread"
template: "minecraft:bread"
addition: "minecraft:bread"
result: "minecraft:iron_sword"
# Optional params
# copy_components: true # false by default
</code-block>
</tab>
<tab title="Brewing">
<code-block lang="YAML">
type: minecraft:potion_mix
id: mycontent:myrecipe
input: "minecraft:water_bottle"
ingredient: "minecraft:bread"
result: "minecraft:iron_sword"
</code-block>
</tab>
</tabs>