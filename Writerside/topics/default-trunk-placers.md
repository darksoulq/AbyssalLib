# Default Trunk Placers
<link-summary>Reference guide for built-in tree trunk placers</link-summary>

Trunk placers define the structural shape and branching logic of the log blocks when generating a tree feature. Below is a list of all default trunk placers included in AbyssalLib.

### StraightTrunkPlacer
**ID:** `abyssallib:straight`

Generates a straight vertical column of blocks.

*(No parameters required)*

---

### ForkingTrunkPlacer
**ID:** `abyssallib:forking`

Generates a main vertical stem that sprouts diagonal branches from its upper section.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>branch_count</code></td>
<td>The maximum number of branches the trunk will attempt to generate.</td>
</tr>
<tr>
<td><code>branch_length</code></td>
<td>The maximum length of each branch in blocks.</td>
</tr>
</table>

---

### GiantTrunkPlacer
**ID:** `abyssallib:giant`

Generates a 2x2 vertical column of blocks.

*(No parameters required)*

---

### BendingTrunkPlacer
**ID:** `abyssallib:bending`

Generates a straight vertical column that bends diagonally in a random horizontal direction.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>bend_length</code></td>
<td>The length of the angled growth section in blocks.</td>
</tr>
</table>

---

### UpwardBranchingTrunkPlacer
**ID:** `abyssallib:upward_branching`

Generates a primary vertical stem with multiple upward-reaching branches.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>branch_count</code></td>
<td>The maximum number of branches to generate.</td>
</tr>
<tr>
<td><code>branch_length</code></td>
<td>The maximum length of each branch extension.</td>
</tr>
</table>