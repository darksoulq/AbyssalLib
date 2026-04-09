# Default Criterion
<secondary-label ref="wip"/>
<link-summary>Reference guide for built-in advancement criteria</link-summary>

Criteria define the specific conditions or actions a player must complete to unlock an advancement. Below is a list of all default criteria included in AbyssalLib.

### AutoCraftCriterion
**ID:** `abyssallib:auto_grant`

Automatically grants progress for this criterion when the player joins the server.

*(No parameters required)*

---

### ItemHasCriterion
**ID:** `abyssallib:has_item`

Checks if any item within the player's inventory matches the provided predicate.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>predicate</code></td>
<td>The item predicate to check against.</td>
</tr>
</table>

---

### StatisticCriterion
**ID:** `abyssallib:statistic`

Checks the value of a specific vanilla Minecraft statistic.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>statistic</code></td>
<td>The statistic to check.</td>
</tr>
<tr>
<td><code>threshold</code></td>
<td>The value needed for the criterion to be awarded.</td>
</tr>
</table>

---

### LevelCriterion
**ID:** `abyssallib:level`

Awards the criterion if the player's experience level matches or exceeds the given level.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>level</code></td>
<td>The experience level needed to grant this criterion.</td>
</tr>
</table>

---

### CustomStatisticCriterion
**ID:** `abyssallib:custom_statistic`

Checks the value of a custom statistic against a threshold.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>statistic</code></td>
<td>The custom statistic ID to check.</td>
</tr>
<tr>
<td><code>threshold</code></td>
<td>The value required for the criterion to be granted (can be a float, integer, or boolean).</td>
</tr>
</table>

---

### CustomAttributeCriterion
**ID:** `abyssallib:custom_attribute`

Checks the value of a custom attribute against a threshold.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>attribute</code></td>
<td>The custom attribute ID to check.</td>
</tr>
<tr>
<td><code>threshold</code></td>
<td>The value required for the criteria to be granted (can be an integer, float, double, or any other Number type).</td>
</tr>
</table>

---

### ItemCraftedCriterion
**ID:** `WIP`

*(Work in progress)*

---

### EntityKilledCriterion
**ID:** `abyssallib:entity_killed`

Checks if the player has killed a specified amount of a given entity type.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>entity_type</code></td>
<td>The ID of the entity to check (currently restricted to vanilla entities, e.g., <code>"minecraft:zombie"</code>).</td>
</tr>
<tr>
<td><code>amount</code></td>
<td>The number of this entity type that must be killed.</td>
</tr>
</table>

---

### BlockMinedCriterion
**ID:** `abyssallib:block_mined`

Checks if the player has mined a specified amount of a given block material.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>material</code></td>
<td>The material of the block to check for (refer to the Paper Javadocs).</td>
</tr>
<tr>
<td><code>amount</code></td>
<td>The amount of blocks of this material that must be broken.</td>
</tr>
</table>

---

### LocationCriterion
**ID:** `abyssallib:location`

Checks if the player is currently located inside a specific world and/or biome.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>world</code></td>
<td>The name of the world the player must be in.</td>
</tr>
<tr>
<td><code>biome</code></td>
<td>The ID of the biome the player must be in.</td>
</tr>
</table>

---

### PotionEffectCriterion
**ID:** `abyssallib:potion_effect`

Checks if the player currently has the provided potion effect.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>effect</code></td>
<td>The effect type ID the player must have.</td>
</tr>
</table>