# Default State Providers
<link-summary>Reference guide for built-in BlockStateProviders</link-summary>

State Providers define the exact block state that is placed into the world when a feature generates. Below is a list of all default state providers included in AbyssalLib.

### SimpleBlockStateProvider
**ID:** `abyssallib:simple`

Provides a single, static block state for every placement.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state</code></td>
<td>A BlockInfo object defining the block to place.</td>
</tr>
</table>

---

### WeightedBlockStateProvider
**ID:** `abyssallib:weighted`

Selects a random block state from a pool of defined entries based on their assigned weights.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>entries</code></td>
<td>A list of weighted block info entries, formatted as a collection of block info objects and their corresponding integer weights.</td>
</tr>
</table>

---

### NoiseThresholdBlockStateProvider
**ID:** `abyssallib:noise_threshold`

Selects between two different block states based on 2D Simplex Noise evaluation at the placement coordinates.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>scale</code></td>
<td>The frequency multiplier applied to the coordinates before sampling the noise.</td>
</tr>
<tr>
<td><code>threshold</code></td>
<td>The breakpoint value evaluating which state is returned.</td>
</tr>
<tr>
<td><code>normal_state</code></td>
<td>The BlockInfo returned if the evaluated noise is less than or equal to the threshold.</td>
</tr>
<tr>
<td><code>high_state</code></td>
<td>The BlockInfo returned if the evaluated noise exceeds the threshold.</td>
</tr>
</table>

---

### RotatedBlockStateProvider
**ID:** `abyssallib:rotated`

Intercepts a base provider and applies forced rotation or directional properties to the resulting block state.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>base_provider</code></td>
<td>The base BlockStateProvider to use and apply rotations to.</td>
</tr>
<tr>
<td><code>axis</code></td>
<td>The forced axis alignment (e.g., used for logs and pillars).</td>
</tr>
<tr>
<td><code>facing</code></td>
<td>The forced directional facing (e.g., used for stairs, chests, and vines).</td>
</tr>
<tr>
<td><code>rotation</code></td>
<td>The forced 16-point rotation (e.g., used for standing signs, banners, and skulls).</td>
</tr>
</table>