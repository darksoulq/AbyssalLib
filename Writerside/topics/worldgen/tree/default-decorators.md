# Default Decorators
<link-summary>Reference guide for built-in tree decorators</link-summary>

Decorators are applied after a tree's trunk, foliage, and roots have been generated. They add secondary features like vines, cocoa beans, or ground modifications. Below is a list of all default decorators included in AbyssalLib.

### AlterGroundDecorator
**ID:** `abyssallib:alter_ground`

Alters the ground surface directly beneath the generated tree.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>provider</code></td>
<td>The block state provider supplying the replacement ground blocks.</td>
</tr>
</table>

---

### LeaveVineDecorator
**ID:** `abyssallib:leave_vine`

Hangs vines downwards from the generated foliage canopy.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>probability</code></td>
<td>The probability (between 0.0 and 1.0) of generating a vine on a given leaf block.</td>
</tr>
</table>

---

### TrunkVineDecorator
**ID:** `abyssallib:trunk_vine`

Generates vines on the exposed sides of the tree trunk.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>probability</code></td>
<td>The probability (between 0.0 and 1.0) of generating a vine on an exposed trunk face.</td>
</tr>
</table>

---

### CocoaBeanDecorator
**ID:** `abyssallib:cocoa_bean`

Generates cocoa beans on the exposed sides of the tree trunk.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>probability</code></td>
<td>The probability (between 0.0 and 1.0) of placing a cocoa bean pod on an exposed trunk face.</td>
</tr>
</table>

---

### BeeNestDecorator
**ID:** `abyssallib:bee_nest`

Attaches a bee nest to the side of the trunk just below the foliage.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>probability</code></td>
<td>The probability (between 0.0 and 1.0) of generating a bee nest on the tree.</td>
</tr>
</table>