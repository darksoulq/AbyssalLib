# Default Root Placers
<link-summary>Reference guide for built-in tree root placers</link-summary>

Root placers define the generation logic for subterranean or exposed root structures when generating a tree feature. Below is a list of all default root placers included in AbyssalLib.

### MangroveRootPlacer
**ID:** `abyssallib:mangrove`

Generates sprawling, arching structural roots above and below the surface, typical of mangrove trees.

*(No parameters required)*

---

### SpreadingRootPlacer
**ID:** `abyssallib:spreading`

Generates a network of roots that spread outward and downward from the base of the trunk, replacing existing ground blocks.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>radius</code></td>
<td>The maximum horizontal distance the roots can spread from the base of the trunk.</td>
</tr>
<tr>
<td><code>depth</code></td>
<td>The maximum vertical depth the roots can reach below the surface.</td>
</tr>
</table>