# Default Rewards
<link-summary>Reference guide for built-in advancement rewards</link-summary>

Rewards define what is granted to a player immediately upon completing an advancement. Below is a list of all default rewards included in AbyssalLib.

### ItemReward
**ID:** `abyssallib:item`

Gives the player a specific item stack.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>item</code></td>
<td>The item stack to award.</td>
</tr>
</table>

---

### ExperienceReward
**ID:** `abyssallib:experience`

Gives the player a specified amount of experience points.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>amount</code></td>
<td>The amount of experience to award.</td>
</tr>
</table>

---

### CommandReward
**ID:** `abyssallib:command`

Executes a server command.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>command</code></td>
<td>The command string to run. You can use <code>&percnt;player&percnt;</code> as a placeholder for the player's name.</td>
</tr>
</table>

---

### PotionEffectReward
**ID:** `abyssallib:potion_effect`

Applies a potion effect to the player.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>type</code></td>
<td>The <code>PotionEffectType</code> ID to apply.</td>
</tr>
<tr>
<td><code>amplifier</code></td>
<td>The level of the effect (0 is level I).</td>
</tr>
<tr>
<td><code>duration</code></td>
<td>The duration of the effect in ticks.</td>
</tr>
<tr>
<td><code>ambient</code></td>
<td>Boolean; whether the effect comes from an ambient source like a beacon.</td>
</tr>
<tr>
<td><code>particles</code></td>
<td>Boolean; whether particles should be visible.</td>
</tr>
<tr>
<td><code>icon</code></td>
<td>Boolean; whether the effect icon should be visible in the HUD.</td>
</tr>
</table>

---

### LootTableReward
**ID:** `abyssallib:loot_table`

Grants the player the contents of a registered Minecraft loot table.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>loot_table</code></td>
<td>The namespaced ID of the registered loot table to execute.</td>
</tr>
</table>

---

### CustomLootTableReward
**ID:** `abyssallib:custom_loot_table`

Grants the player items based on a custom loot table. You must provide either a registered ID or an inline definition.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>id</code></td>
<td>The ID of a registered custom loot table. (Mutually exclusive with <code>table</code>).</td>
</tr>
<tr>
<td><code>table</code></td>
<td>The raw, inline loot table definition. (Mutually exclusive with <code>id</code>).</td>
</tr>
</table>