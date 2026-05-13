# Default Component Formats
<link-summary>Reference guide for serialization formats of the first batch of default data components</link-summary>

Data Components allow you to attach arbitrary or strict vanilla data to an `ItemStack`. When creating items via configuration files or the `ItemPredicate` API, you will need to define these components using their specific Codec format.

Below is the serialized format reference for the built-in Data Components in AbyssalLib.

### CustomMarker
**ID:** `abyssallib:marker`

An internal identifier utilized strictly by the library to track custom items. Do not manually define or modify this component in your configuration files.

---

### BlockItem
**ID:** `abyssallib:block_item`

Maps an item specifically to a registered Custom Block. When a player places an item containing this component, the specified custom block will be placed in the world.

**Format:**
Requires a single string representing the registered ID of the custom block.

```YAML
abyssallib:block_item: "abyssallib_example:mythril_ore"
```

---

### ItemAttributeModifier
**ID:** `minecraft:attribute_modifiers`

Applies specific attribute modifiers (e.g., Attack Damage, Movement Speed) when the item is equipped or held.

**Format:**
Requires a normal map where the top-level keys are the namespaced Attribute IDs. The inner map uses the equipment slot group as the key, mapped to the modifier data object.

* `key`: The unique namespaced key of the modifier.
* `amount`: The numeric value of the modifier.
* `operation`: Any valid Bukkit `AttributeModifier.Operation` enum name (e.g., `ADD_NUMBER`, `ADD_SCALAR`, `MULTIPLY_SCALAR_1`).
* `slot`: The equipment slot group (`MAINHAND`, `OFFHAND`, `ANY`, `ARMOR`, etc.). Note that this should match the key of the inner map.

```YAML
minecraft:attribute_modifiers:
  "minecraft:attack_damage":
    "MAINHAND":
      key: "abyssallib_example:bonus_damage"
      amount: 5.0
      operation: "ADD_NUMBER"
      slot: "MAINHAND"
```

---

### BannerPatterns
**ID:** `minecraft:banner_patterns`

Defines the layered patterns applied to a banner or shield item.

**Format:**
Requires a list of pattern objects defining a `color` and a `pattern` type.

* `color`: A standard DyeColor name (e.g., `RED`, `LIGHT_BLUE`).
* `pattern`: The namespaced key of the banner pattern (e.g., `minecraft:skull`, `minecraft:creeper`).

```YAML
minecraft:banner_patterns:
  - color: "BLACK"
    pattern: "minecraft:skull"
  - color: "RED"
    pattern: "minecraft:cross"
```

---

### BlockAttacks
**ID:** `minecraft:blocks_attacks`

Configures the item's ability to block incoming attacks, functioning similarly to a shield.

**Format:**
Requires an object defining the blocking behavior, cooldowns, and damage reduction rules.

* `block_delay_seconds`: The delay before the block becomes active.
* `disable_cooldown_scale`: Cooldown multiplier applied if the block is disabled.
* `damage_reductions`: List of reduction rules for specific damage types.
* `item_damage`: Function dictating how the item takes durability damage.
* `bypassed_by`: (Optional) A list of damage type keys that bypass this block.
* `block_sound` / `disable_sound`: (Optional) Sound event keys.

```YAML
minecraft:blocks_attacks:
  block_delay_seconds: 0.0
  disable_cooldown_scale: 1.0
  damage_reductions:
    - type:
        - "minecraft:arrow"
        - "minecraft:explosion"
      horizontal_blocking_angle: 90.0
      base: 0.0
      factor: 1.0
  item_damage:
    threshold: 3.0
    base: 1.0
    factor: 0.0
  bypassed_by:
    - "minecraft:magic"
  block_sound: "minecraft:item.shield.block"
  disable_sound: "minecraft:item.shield.break"
```

---

### BlockData
**ID:** `minecraft:block_data`

*(Currently unusable/unimplemented in the Codec API)*

---

### BreakSound
**ID:** `minecraft:break_sound`

Defines the sound event played when the item's durability reaches zero and it breaks.

**Format:**
Requires a single string representing the sound event key.

```YAML
minecraft:break_sound: "minecraft:entity.item.break"
```

---

### BundleContent
**ID:** `minecraft:bundle_contents`

Defines the inventory contents stored within a bundle item.

**Format:**
Requires a list of serialized `ItemStack` objects. Items can be defined simply by their string ID, or fully expanded to include `amount` and nested `data` components.

```YAML
minecraft:bundle_contents:
  - "minecraft:diamond"
  - id: "minecraft:stick"
    amount: 16
    data:
      "minecraft:custom_name": "<red>Special Stick</red>"
```

---

### CanBreak
**ID:** `minecraft:can_break`

Allows the item to break specific blocks when the player is in Adventure mode.

**Format:**
Requires a list of block predicates. Because this relies on Paper's `ItemAdventurePredicate`—which evaluates a list of *multiple predicates* rather than a single flat list of blocks—this is represented as a list of arrays. Each inner array represents a single predicate containing the namespaced keys of the blocks it matches.

```YAML
minecraft:can_break:
  - [ "minecraft:stone", "minecraft:cobblestone" ]
  - [ "minecraft:dirt", "minecraft:grass_block" ]
```

---

### CanPlaceOn
**ID:** `minecraft:can_place_on`

Allows the item (if it is a block) to be placed on top of specific blocks when the player is in Adventure mode.

**Format:**
Requires a list of block predicates. Just like `can_break`, this uses Paper's nested predicate system. It expects a list of arrays, where each inner array contains the namespaced block keys for that specific predicate.

```YAML
minecraft:can_place_on:
  - [ "minecraft:sand", "minecraft:gravel" ]
  - [ "minecraft:iron_block" ]
```

---

### ChargedProjectiles
**ID:** `minecraft:charged_projectiles`

Defines the loaded projectiles within a crossbow item.

**Format:**
Requires a list of serialized `ItemStack` objects. Projectiles can be defined simply by their string ID, or fully expanded to include `amount` and nested `data` components.

```YAML
minecraft:charged_projectiles:
  - "minecraft:arrow"
  - id: "minecraft:firework_rocket"
    amount: 1
    data:
      "minecraft:fireworks":
        flight_duration: 2
        effects:
          - type: "BURST"
            colors: [ {red: 255, green: 0, blue: 0, alpha: 255} ]
            flicker: true
            trail: false
```

---

### CustomData
**ID:** `minecraft:custom_data`

Attaches arbitrary data (equivalent to vanilla custom NBT) to the item. This is incredibly useful for storing custom plugin values that don't fit into standard components.

**Format:**
Requires a standard map of key-value pairs. Values can be strings, booleans, numbers, lists, or nested maps.

```YAML
minecraft:custom_data:
  my_custom_string: "Hello World"
  my_custom_number: 42
  my_custom_flag: true
  my_nested_data:
    level: 5
    tier: "LEGENDARY"
```

---

### Consumable
**ID:** `minecraft:consumable`

Defines how an item is consumed (e.g., eaten or drunk) and the effects applied to the player upon consumption.

**Format:**
Requires an object defining the consumption parameters and a list of `consume_effects`.

* `consume_seconds`: The time in seconds it takes to consume the item.
* `animation`: The animation type (`NONE`, `EAT`, `DRINK`, `BLOCK`, `BOW`, `SPEAR`, `CROSSBOW`, `SPYGLASS`, `TOOT_HORN`, `BRUSH`).
* `sound`: The namespaced key of the sound event played during consumption.
* `has_consume_particles`: Whether the item emits particles while being consumed.
* `consume_effects`: A list of effect objects. Valid types are `apply_effects`, `remove_effects`, `clear_all_effects`, `teleport_randomly`, and `play_sound`.

<tabs>
<tab title="Apply Effects">

Applies one or more status effects to the player with a specific probability.

```YAML
- type: "apply_effects"
  probability: 1.0
  effects:
    - type: "minecraft:regeneration"
      duration: 100
      amplifier: 1
      ambient: false
      particles: true
      icon: true
```

</tab>
<tab title="Remove Effects">

Removes specific status effects from the player.

```YAML
- type: "remove_effects"
  effects:
    - "minecraft:poison"
    - "minecraft:wither"
```

</tab>
<tab title="Clear All Effects">

Clears every active status effect from the player.

```YAML
- type: "clear_all_effects"
```

</tab>
<tab title="Teleport Randomly">

Teleports the player randomly within the specified diameter (functions like a Chorus Fruit).

```YAML
- type: "teleport_randomly"
  diameter: 16.0
```

</tab>
<tab title="Play Sound">

Plays a specific sound event to the player.

```YAML
- type: "play_sound"
  sound: "minecraft:entity.player.burp"
```

</tab>
</tabs>

**Full Example:**
```YAML
minecraft:consumable:
  consume_seconds: 1.6
  animation: "EAT"
  sound: "minecraft:entity.generic.eat"
  has_consume_particles: true
  consume_effects:
    - type: "apply_effects"
      probability: 1.0
      effects:
        - type: "minecraft:regeneration"
          duration: 100
          amplifier: 1
          ambient: false
          particles: true
          icon: true
    - type: "play_sound"
      sound: "minecraft:entity.player.burp"
```

---

### ContainerLoot
**ID:** `minecraft:container_loot`

Links the item (usually a container like a chest or shulker box) to a specific loot table, which will dynamically generate its contents upon placement or opening.

**Format:**
Requires an object containing the namespaced `loot_table` key and a `seed`. Provide `0` as the seed to randomize the loot generation.

```YAML
minecraft:container_loot:
  loot_table: "minecraft:chests/simple_dungeon"
  seed: 0
```

---

### UseCooldown
**ID:** `minecraft:use_cooldown`

Applies a visual and mechanical cooldown to the item immediately after it is used.

**Format:**
Requires the `cooldown` duration in seconds, and an optional `group` key to link cooldowns across multiple items (for example, applying a single cooldown group to all Ender Pearls).

```YAML
minecraft:use_cooldown:
  cooldown: 2.5
  group: "minecraft:ender_pearl"
```

---

### CustomName
**ID:** `minecraft:custom_name`

Overrides the display name of the item.

**Format:**
Requires a single string formatted using standard MiniMessage tags.

```YAML
minecraft:custom_name: "<gradient:gold:yellow>Legendary Blade</gradient>"
```

---

### DeathProtection
**ID:** `minecraft:death_protection`

Configures the item to act as a Totem of Undying, defining the effects applied to the player when the item saves them from death.

**Format:**
Requires a list of `ConsumeEffect` objects. These share the exact same structure as the `consume_effects` list documented in the `minecraft:consumable` component.

```YAML
minecraft:death_protection:
  - type: "clear_all_effects"
  - type: "apply_effects"
    probability: 1.0
    effects:
      - type: "minecraft:regeneration"
        duration: 900
        amplifier: 1
        ambient: false
        particles: true
        icon: true
```

---

### DisplayTooltip
**ID:** `minecraft:tooltip_display`

Controls whether the item's tooltip is hidden entirely, or selectively hides specific component data from appearing in the tooltip.

**Format:**
Requires an object containing a boolean to hide the tooltip globally, and a list of specific components to hide.

* `hide_tooltips`: If `true`, completely hides the entire item tooltip.
* `hidden_components`: A list of namespaced component keys (e.g., `minecraft:unbreakable`, `minecraft:enchantments`) that should not be rendered in the item's lore.

```YAML
minecraft:tooltip_display:
  hide_tooltips: false
  hidden_components:
    - "minecraft:enchantments"
    - "minecraft:attribute_modifiers"
```

---

### Durability
**ID:** `minecraft:damage`

Defines the current amount of damage the item has taken. (Note: To define the *maximum* durability of an item, you must use the `minecraft:max_damage` component instead).

**Format:**
Requires a single integer representing the damage value.

```YAML
minecraft:damage: 15
```

---

### Dye
**ID:** `minecraft:dye`

Applies a base dye color to applicable items (like Leather Armor, Wolf Armor, or glass).

**Format:**
Requires a single string matching a standard Bukkit `DyeColor` name (e.g., `WHITE`, `ORANGE`, `MAGENTA`, `LIGHT_BLUE`).

```YAML
minecraft:dye: "LIGHT_BLUE"
```

---

### DyedColor
**ID:** `minecraft:dyed_color`

Applies a specific ARGB color tint to dyeable items, allowing for precise custom colors beyond the standard vanilla dye variants.

**Format:**
Requires an object defining the exact `alpha`, `red`, `green`, and `blue` color channels. Values must be integers between 0 and 255.

```YAML
minecraft:dyed_color:
  alpha: 255
  red: 150
  green: 50
  blue: 200
```

---

### Enchantable
**ID:** `minecraft:enchantable`

Defines the item's base enchantability value, which determines the quality and quantity of enchantments it receives when placed in an Enchanting Table. For reference, Diamond tools have an enchantability of 10, while Gold tools have 22.

**Format:**
Requires a single integer representing the enchantability value.

```YAML
minecraft:enchantable: 15
```

---

### EnchantmentGlintOverride
**ID:** `minecraft:enchantment_glint_override`

Forces the glowing visual enchantment glint on or off.

**Format:**
Requires a single boolean value. If `true`, the item will always glow even without enchantments. If `false`, the item will never glow, even if it has enchantments applied.

```YAML
minecraft:enchantment_glint_override: true
```

---

### Enchantments
**ID:** `minecraft:enchantments`

Defines the enchantments applied to the item.

**Format:**
Requires a normal map where the keys are the namespaced enchantment IDs and the values are their respective levels as integers.

```YAML
minecraft:enchantments:
  "minecraft:sharpness": 5
  "minecraft:unbreaking": 3
  "minecraft:sweeping_edge": 2
```

---

### Equippable
**ID:** `minecraft:equippable`

Dictates how the item can be equipped, the slot it occupies, and associated sounds, models, and behavioral rules.

**Format:**
Requires an object defining the equipment properties.

* `slot`: The Bukkit `EquipmentSlot` enum name (e.g., `HEAD`, `CHEST`, `LEGS`, `FEET`, `BODY`, `HAND`, `OFF_HAND`).
* `equip_sound`: The namespaced key of the sound played when equipped.
* `asset_id`: (Optional) The namespaced key for the armor model/texture asset.
* `camera_overlay`: (Optional) The namespaced key for a first-person screen overlay (like carved pumpkins).
* `allowed_entities`: (Optional) A list of namespaced entity types allowed to equip this item.
* `dispensable`: Boolean determining if dispensers can equip this item onto targets.
* `swappable`: Boolean determining if the player can equip it via right-click to swap.
* `damage_on_hurt`: Boolean determining if the item loses durability when the wearer takes damage.
* `can_be_sheared`: Boolean determining if the item can be removed by shears.
* `shear_sound`: (Optional) The namespaced key of the sound played when sheared.

```YAML
minecraft:equippable:
  slot: "HEAD"
  equip_sound: "minecraft:item.armor.equip_diamond"
  dispensable: true
  swappable: true
  damage_on_hurt: true
  can_be_sheared: false
```

---

### Fireworks
**ID:** `minecraft:fireworks`

Defines the properties of a firework rocket, including its flight duration and the explosive effects it produces upon detonating.

**Format:**
Requires an object defining the duration and a list of effect objects.

* `flight_duration`: Integer representing the flight time modifier.
* `effects`: A list of explosion effect objects (see `minecraft:firework_explosion` below for the exact structure).

```YAML
minecraft:fireworks:
  flight_duration: 2
  effects:
    - type: "BURST"
      flicker: true
      trail: false
      colors: 
        - {red: 255, green: 0, blue: 0, alpha: 255}
      fade_colors: 
        - {red: 255, green: 255, blue: 0, alpha: 255}
```

---

### FireworkExplosion
**ID:** `minecraft:firework_explosion`

Defines a single firework explosion effect. This is typically applied directly to Firework Star items.

**Format:**
Requires an object defining the visual shape, toggles, and colors.

* `type`: The Bukkit `FireworkEffect.Type` (e.g., `BALL`, `BALL_LARGE`, `STAR`, `CREEPER`, `BURST`).
* `flicker`: Boolean to enable the crackle effect.
* `trail`: Boolean to enable the trailing tail effect.
* `colors`: A list of ARGB color objects defining the primary explosion colors.
* `fade_colors`: A list of ARGB color objects the explosion fades into.

```YAML
minecraft:firework_explosion:
  type: "CREEPER"
  flicker: false
  trail: true
  colors:
    - {red: 0, green: 255, blue: 0, alpha: 255}
  fade_colors: []
```

---

### Food
**ID:** `minecraft:food`

Defines the core nutritional properties of an edible item. (Note: The actual consumption speed, animation, and potion effects are handled by the `minecraft:consumable` component, not here).

**Format:**
Requires an object defining nutrition points and saturation.

* `nutrition`: Integer representing the food points (half-drumsticks) restored.
* `saturation`: Float representing the saturation modifier applied.
* `can_always_eat`: Boolean. If true, the player can eat this item even when their hunger bar is full (e.g., Golden Apples).

```YAML
minecraft:food:
  nutrition: 8
  saturation: 12.8
  can_always_eat: false
```

---

### Glider
**ID:** `minecraft:glider`

Equips the item with Elytra gliding capabilities when worn in the chest slot.

**Format:**
This is a presence component. It does not require any specific data, so it is defined using an empty object or empty string based on the codec.

```YAML
minecraft:glider: ""
```

---

### Instrument
**ID:** `minecraft:instrument`

Defines the specific music instrument sound played when the item (typically a Goat Horn) is used.

**Format:**
Requires a single string representing the namespaced key of the instrument (e.g., `minecraft:ponder_goat_horn`, `minecraft:sing_goat_horn`).

```YAML
minecraft:instrument: "minecraft:ponder_goat_horn"
```

---

### IntangibleProjectile
**ID:** `minecraft:intangible_projectile`

Marks a projectile item (like an arrow) as intangible, meaning it can only be picked up by players in Creative mode after being fired.

**Format:**
This is a presence component. It is defined using an empty string.

```YAML
minecraft:intangible_projectile: ""
```

---

### ItemModel
**ID:** `minecraft:item_model`

Overrides the default visual model of the item to point to a specific model definition in a resource pack.

**Format:**
Requires a single string representing the namespaced key of the model.

```YAML
minecraft:item_model: "abyssallib_example:mythril_sword"
```

---

### ItemName
**ID:** `minecraft:item_name`

Overrides the base name of the item. Unlike `minecraft:custom_name`, this does not automatically italicize the text and is treated as the item's true, inherent name.

**Format:**
Requires a single string formatted using standard MiniMessage tags.

```YAML
minecraft:item_name: "<aqua>Mythril Sword</aqua>"
```

---

### Lore
**ID:** `minecraft:lore`

Defines the lore (description lines) displayed beneath the item's name.

**Format:**
Requires a list of strings, each formatted using standard MiniMessage tags.

```YAML
minecraft:lore:
  - "<gray>Forged in the depths.</gray>"
  - "<red>Requires Level 10</red>"
```

---

### MapColor
**ID:** `minecraft:map_color`

Defines the ambient color of a filled map item.

**Format:**
Requires an object defining the exact `alpha`, `red`, `green`, and `blue` color channels. Values must be integers between 0 and 255.

```YAML
minecraft:map_color:
  alpha: 255
  red: 100
  green: 150
  blue: 50
```

---

### MapDecorates
**ID:** `minecraft:map_decorations`

Defines specific markers and decorations (like player icons, banners, or frames) that appear on a filled map.

**Format:**
Requires a map where the top-level keys are arbitrary string identifiers for the decoration. Each key maps to a decoration entry object.

* `type`: The namespaced key of the map cursor type (e.g., `minecraft:player`, `minecraft:target_x`, `minecraft:red_x`).
* `x`: Double representing the X coordinate on the map.
* `z`: Double representing the Z coordinate on the map.
* `rotation`: Float representing the rotation of the icon.

```YAML
minecraft:map_decorations:
  "treasure_mark":
    type: "minecraft:red_x"
    x: 45.5
    z: -12.0
    rotation: 0.0
```

---

### MapID
**ID:** `minecraft:map_id`

Links a filled map item to its specific map state data stored on the server.

**Format:**
Requires a single integer representing the map's ID.

```YAML
minecraft:map_id: 12
```

---

### MapPostProcess
**ID:** `minecraft:map_post_processing`

Defines the post-processing instruction for a filled map, typically used to lock or scale maps in a cartography table.

**Format:**
Requires a single string matching a valid Bukkit `MapPostProcessing` enum (e.g., `LOCK`, `SCALE`).

```YAML
minecraft:map_post_processing: "LOCK"
```

---

### MaxDurability
**ID:** `minecraft:max_damage`

Defines the maximum durability (damage capacity) of an item.

**Format:**
Requires a single integer representing the maximum damage the item can take before breaking.

```YAML
minecraft:max_damage: 500
```

---

### MaxStackSize
**ID:** `minecraft:max_stack_size`

Defines the maximum amount of this item that can be stacked in a single inventory slot.

**Format:**
Requires a single integer. In modern vanilla, this value can typically be set anywhere from 1 to 99.

```YAML
minecraft:max_stack_size: 16
```

---

### ModelData
**ID:** `minecraft:custom_model_data`

Applies custom model data to an item. This is actively used by resource packs to determine which 3D model or texture to render.

**Format:**
Requires an object defining lists of `floats`, `flags` (booleans), `strings`, and `colors` (ARGB objects).

```YAML
minecraft:custom_model_data:
  floats: [ 1.0, 5.5 ]
  flags: [ true, false ]
  strings: [ "abyssallib_example:custom_sword" ]
  colors:
    - {alpha: 255, red: 255, green: 0, blue: 0}
```

---

### NoteBlockSound
**ID:** `minecraft:note_block_sound`

Defines the sound played when this item (typically a player head or skull) is placed on top of a Note Block.

**Format:**
Requires a single string representing the namespaced sound event key.

```YAML
minecraft:note_block_sound: "minecraft:entity.ender_dragon.ambient"
```

---

### OminousAmplifier
**ID:** `minecraft:ominous_bottle_amplifier`

Defines the amplifier level for an Ominous Bottle, which directly translates to the level of the Bad Omen effect applied when consumed.

**Format:**
Requires a single integer between 0 and 4 (representing Bad Omen I through V).

```YAML
minecraft:ominous_bottle_amplifier: 2
```

---

### PlayableJukebox
**ID:** `minecraft:jukebox_playable`

Makes the item playable in a Jukebox and defines which specific song it will play.

**Format:**
Requires a single string representing the namespaced key of a registered Jukebox song.

```YAML
minecraft:jukebox_playable: "minecraft:music_disc.pigstep"
```

---

### PotDecorates
**ID:** `minecraft:pot_decorations`

Defines the specific items (usually pottery sherds or bricks) adorning the four sides of a Decorated Pot item.

**Format:**
Requires an object mapping the `front`, `back`, `left`, and `right` sides to specific namespaced item keys.

```YAML
minecraft:pot_decorations:
  front: "minecraft:danger_pottery_sherd"
  back: "minecraft:brick"
  left: "minecraft:skull_pottery_sherd"
  right: "minecraft:brick"
```

---

### PotionContent
**ID:** `minecraft:potion_contents`

Defines the base potion type, custom liquid color, and specific custom status effects stored within a potion, splash potion, lingering potion, or tipped arrow.

**Format:**
Requires an object defining the `potion` base type, an optional ARGB `color`, an optional `customName` for the potion, and a list of `customEffects` (sharing the format of `minecraft:potion_effect`).

```YAML
minecraft:potion_contents:
  potion: "STRONG_HEALING"
  color: {alpha: 255, red: 200, green: 0, blue: 50}
  customName: "Elixir of Life"
  customEffects:
    - type: "minecraft:absorption"
      duration: 200
      amplifier: 1
      ambient: false
      particles: true
      icon: true
```

---

### PotionDurationScale
**ID:** `minecraft:potion_duration_scale`

A multiplier applied to the duration of any potion effects granted by this item. This is primarily used by Ominous Bottles and custom Tipped Arrows to dynamically scale effect lengths.

**Format:**
Requires a single float representing the scale multiplier.

```YAML
minecraft:potion_duration_scale: 0.125
```

---

### Profile
**ID:** `minecraft:profile`

Defines the player profile tied to a Player Head item, determining its skin/texture.

**Format:**
Requires an object defining the player's name, UUID, and/or texture properties. All fields are technically optional, but a valid texture requires at least the `properties` list to be populated with the base64 texture string.

* `name`: (Optional) The username of the player.
* `uuid`: (Optional) The UUID of the player.
* `properties`: A list of property objects containing `name`, `value` (the base64 string), and an optional `signature`.

```YAML
minecraft:profile:
  name: "Notch"
  uuid: "069a79f4-44e9-4726-a5be-fca90e38aaf5"
  properties:
    - name: "textures"
      value: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv... "
```

---

### Recipes
**ID:** `minecraft:recipes`

Defines a list of recipes contained within a Knowledge Book item. Upon using the book, these recipes are granted to the player.

**Format:**
Requires a list of strings representing the namespaced keys of the recipes to grant.

```YAML
minecraft:recipes:
  - "minecraft:diamond_sword"
  - "minecraft:iron_pickaxe"
```

---

### RemainderUse
**ID:** `minecraft:use_remainder`

Defines the item left behind after this item is consumed (e.g., the empty Glass Bottle left behind after drinking a Potion).

**Format:**
Requires a serialized `ItemStack` object. This can be a simple string ID, or an expanded object including `amount` and nested `data` components.

```YAML
minecraft:use_remainder:
  id: "minecraft:glass_bottle"
  amount: 1
```

---

### Repairable
**ID:** `minecraft:repairable`

Defines the specific items that can be used in an anvil to repair this item's durability.

**Format:**
Requires a list of strings representing the namespaced item keys that are valid repair materials.

```YAML
minecraft:repairable:
  - "minecraft:iron_ingot"
  - "minecraft:iron_block"
```

---

### RepairCost
**ID:** `minecraft:repair_cost`

Defines the current cumulative anvil penalty cost for the item.

**Format:**
Requires a single integer representing the level cost.

```YAML
minecraft:repair_cost: 3
```

---

### ResistantDamage
**ID:** `minecraft:damage_resistant`

Makes the item completely immune to specific types of damage when dropped on the ground (e.g., how Netherite items do not burn in lava).

**Format:**
Can be defined as a single string, or a list of strings representing the namespaced keys of the damage types to resist.

```YAML
minecraft:damage_resistant:
  - "minecraft:in_fire"
  - "minecraft:lava"
```

---

### ShulkerColor
**ID:** `minecraft:shulker_color`

Overrides the base color of a Shulker Box item.

**Format:**
Requires a single string matching a standard Bukkit `DyeColor` name (e.g., `WHITE`, `PURPLE`, `BLACK`).

```YAML
minecraft:shulker_color: "PURPLE"
```

---

### StoredEnchantments
**ID:** `minecraft:stored_enchantments`

Defines the enchantments stored inside an Enchanted Book. These do not apply to the book itself, but rather dictate what can be transferred via an anvil.

**Format:**
Requires a normal map where the keys are the namespaced enchantment IDs and the values are their respective levels as integers.

```YAML
minecraft:stored_enchantments:
  "minecraft:mending": 1
  "minecraft:silk_touch": 1
```

---

### SuspiciousStewEffect
**ID:** `minecraft:suspicious_stew_effect`

Defines the hidden status effects granted to a player upon consuming this Suspicious Stew item.

**Format:**
Requires a list of effect objects, each containing an `effect_type` and a `duration`.

* `effect_type`: The namespaced key of the potion effect.
* `duration`: The length of the effect in ticks (20 ticks = 1 second).

```YAML
minecraft:suspicious_stew_effect:
  - effect_type: "minecraft:blindness"
    duration: 160
  - effect_type: "minecraft:night_vision"
    duration: 200
```

---

### Tool
**ID:** `minecraft:tool`

Defines the item's behavior when used to mine blocks, including base mining speed, durability taken per block, and specific mining rules for different block types.

**Format:**
Requires an object defining the default tool parameters and a list of specific mining `rules`.

* `default_mining_speed`: Float representing the base mining speed multiplier (1.0 is default).
* `damage_per_block`: Integer representing the durability lost per block mined.
* `can_destroy_blocks_in_creative`: Boolean determining if it breaks blocks instantly in Creative mode.
* `rules`: A list of rule objects containing:
    * `blocks`: A list of namespaced block keys this rule applies to.
    * `speed`: The mining speed multiplier for these specific blocks.
    * `correct_for_drops`: A TriState string (`TRUE`, `FALSE`, `NOT_SET`) determining if mining these blocks yields their standard drops.

```YAML
minecraft:tool:
  default_mining_speed: 1.0
  damage_per_block: 1
  can_destroy_blocks_in_creative: false
  rules:
    - blocks: 
        - "minecraft:cobweb"
      speed: 15.0
      correct_for_drops: "TRUE"
```

---

### TooltipStyle
**ID:** `minecraft:tooltip_style`

Overrides the visual background and styling of the item's tooltip UI.

**Format:**
Requires a single string representing the namespaced key of the GUI sprite/texture.

```YAML
minecraft:tooltip_style: "minecraft:special"
```

---

### TrackerLodestone
**ID:** `minecraft:lodestone_tracker`

Links a compass item to a specific location, typically a Lodestone, causing the compass needle to point toward it.

**Format:**
Requires an object defining the target `location` and whether the compass is actively being `tracked`.

* `tracked`: Boolean. If true, the compass will lose its target if the lodestone block is broken.
* `location`: A serialized Location object requiring `world` (namespaced key), `x`, `y`, `z`, `yaw`, and `pitch`.

```YAML
minecraft:lodestone_tracker:
  tracked: true
  location:
    world: "minecraft:overworld"
    x: 150.0
    y: 64.0
    z: -200.0
    yaw: 0.0
    pitch: 0.0
```

---

### Trim
**ID:** `minecraft:trim`

Applies an armor trim to the item, altering its visual texture.

**Format:**
Requires an object defining the `material` (color) and `pattern` (design) of the trim.

* `material`: The namespaced key of the trim material (e.g., `minecraft:quartz`, `minecraft:gold`, `minecraft:amethyst`).
* `pattern`: The namespaced key of the trim pattern (e.g., `minecraft:silence`, `minecraft:ward`, `minecraft:coast`).

```YAML
minecraft:trim:
  material: "minecraft:quartz"
  pattern: "minecraft:silence"
```

---

### Unbreakable
**ID:** `minecraft:unbreakable`

Makes the item completely immune to durability loss.

**Format:**
This is a presence component. It does not require any specific data and is defined using an empty string.

```YAML
minecraft:unbreakable: ""
```

---

### Weapon
**ID:** `minecraft:weapon`

Defines the item's combat properties when used to attack an entity.

**Format:**
Requires an object defining the durability loss and shield-disabling mechanics.

* `item_damage_per_attack`: Integer representing the durability lost when striking an entity.
* `disable_blocking_for_seconds`: Float representing how long (in seconds) the target's shield is disabled if they block the attack.

```YAML
minecraft:weapon:
  item_damage_per_attack: 2
  disable_blocking_for_seconds: 1.5
```

---

### WritableBookContents
**ID:** `minecraft:writable_book_content`

Defines the raw text content of a Book and Quill item.

**Format:**
Requires a list of strings, where each string represents a single page of raw text.

```YAML
minecraft:writable_book_content:
  - "This is the first page of my diary."
  - "This is the second page."
```

---

### WrittenBookContents
**ID:** `minecraft:written_book_content`

Defines the properties and read-only content of a Written Book item.

**Format:**
Requires an object defining the book's metadata and a list of formatted pages.

* `title`: The title of the book.
* `author`: The author's name.
* `generation`: Integer representing the book's copy status (0 = Original, 1 = Copy, 2 = Copy of a Copy, 3 = Tattered).
* `resolved`: Boolean determining if the text components inside have already been parsed.
* `pages`: A list of strings formatted using standard MiniMessage tags.

```YAML
minecraft:written_book_content:
  title: "The Ancient Texts"
  author: "Notch"
  generation: 0
  resolved: true
  pages:
    - "<dark_red>Warning:</dark_red> Do not read aloud."
    - "It was a dark and stormy night..."
```