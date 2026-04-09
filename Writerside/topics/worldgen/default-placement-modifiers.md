# Default Placement Modifiers
<link-summary>Reference guide for built-in placement modifiers</link-summary>

Placement modifiers add conditions for the placement of features and dynamically modify where they generate in the world. Below is a list of all default placement modifiers included in AbyssalLib.

### CountModifier
**ID:** `abyssallib:count`

Duplicates the incoming position stream a specified number of times.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>count</code></td>
<td>The number of times to duplicate the input positions.</td>
</tr>
</table>

---

### HeightRangeModifier
**ID:** `abyssallib:height_range`

Randomizes the Y-coordinate of input positions between a specified minimum and maximum bounds.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>min_inclusive</code></td>
<td>The minimum allowed Y coordinate (inclusive).</td>
</tr>
<tr>
<td><code>max_inclusive</code></td>
<td>The maximum allowed Y coordinate (inclusive).</td>
</tr>
</table>

---

### InSquareModifier
**ID:** `abyssallib:in_square`

Spreads the input positions randomly across the X and Z axes within the bounds of a standard 16x16 chunk.

*(No parameters required)*

---

### EnvironmentScanModifier
**ID:** `abyssallib:environment_scan`

Performs a vertical scan to find the nearest solid surface.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>max_steps</code></td>
<td>The maximum number of blocks to scan in the specified direction.</td>
</tr>
<tr>
<td><code>up</code></td>
<td>Determines the scan direction; <code>true</code> to scan upwards, <code>false</code> to scan downwards.</td>
</tr>
</table>

---

### BlockFilterModifier
**ID:** `abyssallib:block_filter`

Filters out incoming positions based on the target block type.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>targets</code></td>
<td>The list of allowed block info targets.</td>
</tr>
<tr>
<td><code>offset</code></td>
<td>The coordinate offset to check relative to the current placement position.</td>
</tr>
</table>

---

### BiomeFilterModifier
**ID:** `abyssallib:biome_filter`

Filters positions based on the active biome at the coordinates.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>allowed_biomes</code></td>
<td>The list of acceptable biome identifiers (e.g., <code>"minecraft:plains"</code>).</td>
</tr>
</table>

---

### ChanceModifier
**ID:** `abyssallib:chance`

Filters positions based on a random probability.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>chance</code></td>
<td>The denominator for the 1-in-X probability. Must be >= 1.</td>
</tr>
</table>

---

### RandomOffsetModifier
**ID:** `abyssallib:random_offset`

Slightly shifts the incoming position randomly along all three axes based on the defined spread values.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>xz_spread</code></td>
<td>The maximum horizontal distance (positive or negative) to offset the position.</td>
</tr>
<tr>
<td><code>y_spread</code></td>
<td>The maximum vertical distance (positive or negative) to offset the position.</td>
</tr>
</table>

---

### HeightmapModifier
**ID:** `abyssallib:heightmap`

Snaps the Y-coordinate of incoming positions to the highest block at that specific X and Z coordinate, dictated by a selected heightmap.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>heightmap</code></td>
<td>The heightmap projection to use when calculating the surface.</td>
</tr>
</table>

---

### RarityFilterModifier
**ID:** `abyssallib:rarity_filter`

Acts as a per-chunk probability gate. Unlike the standard ChanceModifier which evaluates each generated vector individually, this modifier evaluates the probability exactly once per stream execution.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>chance</code></td>
<td>The denominator for the 1-in-X probability per chunk.</td>
</tr>
</table>

---

### WaterDepthFilterModifier
**ID:** `abyssallib:water_depth_filter`

Filters positions based on the depth of the water column above them.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>max_depth</code></td>
<td>The maximum allowed number of contiguous water blocks directly above the position.</td>
</tr>
</table>

---

### NoiseThresholdModifier
**ID:** `abyssallib:noise_threshold`

Filters positions based on 2D simplex noise.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>frequency</code></td>
<td>The frequency multiplier applied to coordinates before sampling noise.</td>
</tr>
<tr>
<td><code>threshold</code></td>
<td>The threshold value to compare the sampled noise against.</td>
</tr>
<tr>
<td><code>above_threshold</code></td>
<td>Whether the noise value must be above or below the threshold.</td>
</tr>
</table>

---

### NoiseCountModifier
**ID:** `abyssallib:noise_count`

Duplicates the incoming position stream a variable number of times based on a 2D simplex noise evaluation.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>frequency</code></td>
<td>The frequency multiplier applied to coordinates before sampling the noise field.</td>
</tr>
<tr>
<td><code>threshold</code></td>
<td>The noise value boundary determining which count is used.</td>
</tr>
<tr>
<td><code>count_above</code></td>
<td>The number of positions to yield if the sampled noise is strictly greater than the threshold.</td>
</tr>
<tr>
<td><code>count_below</code></td>
<td>The number of positions to yield if the sampled noise is less than or equal to the threshold.</td>
</tr>
</table>

---

### SurfaceRelativeThresholdModifier
**ID:** `abyssallib:surface_relative_threshold`

Filters positions based on their vertical proximity to a selected world heightmap (surface).

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>heightmap</code></td>
<td>The heightmap criteria used to locate the surface block.</td>
</tr>
<tr>
<td><code>min_inclusive</code></td>
<td>The minimum allowed relative offset from the surface (inclusive).</td>
</tr>
<tr>
<td><code>max_inclusive</code></td>
<td>The maximum allowed relative offset from the surface (inclusive).</td>
</tr>
</table>

---

### BiomeTransitionModifier
**ID:** `abyssallib:biome_transition`

Prevents feature bleeding across chunk boundaries.

*(No parameters required)*

---

### CountOnEveryLayerModifier
**ID:** `abyssallib:count_on_every_layer`

Duplicates the incoming position stream across every single Y-coordinate from the bottom of the world to the top.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>count</code></td>
<td>The number of placement attempts to make per Y-layer.</td>
</tr>
</table>

---

### FixedPlacementModifier
**ID:** `abyssallib:fixed_placement`

Completely overrides the procedural generation stream, replacing it with a fixed, hardcoded list of specific coordinates.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>positions</code></td>
<td>The static list of absolute world coordinates.</td>
</tr>
</table>