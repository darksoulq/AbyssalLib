# Default Foliage Placers
<link-summary>Reference guide for built-in tree foliage placers</link-summary>

Foliage placers define the shape, spread, and placement logic of leaf blocks when generating a tree feature. Below is a list of all default foliage placers included in AbyssalLib.

### BlobFoliagePlacer
**ID:** `abyssallib:blob`

Generates a layered spherical blob of leaves.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>height</code></td>
<td>The vertical height (layers) of the foliage blob.</td>
</tr>
</table>

---

### PineFoliagePlacer
**ID:** `abyssallib:pine`

Generates a conical, layered structure.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>height</code></td>
<td>The vertical height (layers) of the conical foliage section.</td>
</tr>
</table>

---

### RandomSpreadFoliagePlacer
**ID:** `abyssallib:random_spread`

Scatters individual leaf blocks randomly within a bounding area.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>height</code></td>
<td>The vertical boundary (in layers below the attachment point) where leaves can spawn.</td>
</tr>
<tr>
<td><code>attempts</code></td>
<td>The number of times the algorithm will attempt to place a leaf block.</td>
</tr>
</table>

---

### AcaciaFoliagePlacer
**ID:** `abyssallib:acacia`

Generates a flat, horizontal canopy typical of acacia trees.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>radius</code></td>
<td>The horizontal radius governing the foliage spread.</td>
</tr>
</table>

---

### DarkOakFoliagePlacer
**ID:** `abyssallib:dark_oak`

Generates a thick, cuboid canopy typical of dark oak trees.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>radius</code></td>
<td>The horizontal radius governing the foliage spread.</td>
</tr>
</table>