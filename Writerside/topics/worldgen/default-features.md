# Default Features
<link-summary>Reference guide for built-in world generation features</link-summary>

Features define the specific logic used to generate structures, blocks, or modifications in the world during chunk generation. Below is a list of all default features included in AbyssalLib.

### SimpleBlockFeature
**ID:** `abyssallib:simple_block`

Generates a single block at the target location.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider defining the block to place.</td>
</tr>
</table>

---

### OreFeature
**ID:** `abyssallib:ore`

Generates clustered veins of blocks, typically used for ores or dirt patches.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of replacement rules. Each rule specifies a <code>target</code> (the block info to be replaced) and a <code>state_provider</code> (the new block state to place).</td>
</tr>
<tr>
<td><code>size</code></td>
<td>The maximum number of blocks that can be generated in a single vein cluster.</td>
</tr>
</table>

---

### BlockPatchFeature
**ID:** `abyssallib:block_patch`

Scatters a specific block around a central origin point, simulating natural patches like flowers or grass.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>tries</code></td>
<td>The maximum number of placement attempts within the patch radius.</td>
</tr>
<tr>
<td><code>xz_spread</code></td>
<td>The maximum horizontal distance from the origin (X and Z axes) to attempt placement.</td>
</tr>
<tr>
<td><code>y_spread</code></td>
<td>The maximum vertical distance from the origin (Y axis) to attempt placement.</td>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider supplying the blocks to be scattered.</td>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of valid target blocks that the patch is allowed to overwrite or place upon.</td>
</tr>
</table>

---

### LakeFeature
**ID:** `abyssallib:lake`

Generates a pool of liquid surrounded by an optional barrier block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>fluid_provider</code></td>
<td>The block state provider defining the liquid filling the lake.</td>
</tr>
<tr>
<td><code>barrier_provider</code></td>
<td>(Optional) The block state provider defining the outer casing/rim of the lake.</td>
</tr>
</table>

---

### DiskFeature
**ID:** `abyssallib:disk`

Generates a flat, circular disk of blocks, commonly used for underwater clay or sand patches.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider supplying the blocks for the disk.</td>
</tr>
<tr>
<td><code>radius</code></td>
<td>The horizontal radius of the disk in blocks.</td>
</tr>
<tr>
<td><code>half_height</code></td>
<td>The vertical thickness offset applied above and below the origin.</td>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of valid blocks that the disk is allowed to replace.</td>
</tr>
</table>

---

### StructureFeature
**ID:** `abyssallib:structure`

Places a pre-defined custom structure into the world.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>structure_id</code></td>
<td>The namespaced registry key of the target structure to load.</td>
</tr>
<tr>
<td><code>random_rotation</code></td>
<td>(Optional) A boolean to randomize rotation, overriding the configured rotation.</td>
</tr>
<tr>
<td><code>rotation</code></td>
<td>(Optional) The explicitly defined structural rotation.</td>
</tr>
<tr>
<td><code>random_mirror</code></td>
<td>(Optional) A boolean to randomize mirroring, overriding the configured mirror.</td>
</tr>
<tr>
<td><code>mirror</code></td>
<td>(Optional) The explicitly defined structural mirror.</td>
</tr>
</table>

---

### SpringFeature
**ID:** `abyssallib:spring`

Generates a single fluid source block enclosed within rock, simulating a natural spring.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider representing the liquid source block.</td>
</tr>
<tr>
<td><code>rock</code></td>
<td>A list of blocks considered valid encasing materials.</td>
</tr>
<tr>
<td><code>requires_blocks_below</code></td>
<td>A boolean dictating if the block directly beneath the origin must be a valid rock block.</td>
</tr>
<tr>
<td><code>hole_count</code></td>
<td>The required number of adjacent air blocks for the spring to generate.</td>
</tr>
<tr>
<td><code>valid_neighbors</code></td>
<td>The required number of adjacent rock blocks for the spring to generate.</td>
</tr>
</table>

---

### GeodeFeature
**ID:** `abyssallib:geode`

Generates a multi-layered spherical structure with an outer shell, an inner cavity, and internal decorations.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>outer_wall_provider</code></td>
<td>The block provider for the outermost protective shell.</td>
</tr>
<tr>
<td><code>middle_wall_provider</code></td>
<td>The block provider for the intermediate transition layer.</td>
</tr>
<tr>
<td><code>inner_wall_provider</code></td>
<td>The block provider for the inner geode crust.</td>
</tr>
<tr>
<td><code>filling_provider</code></td>
<td>The block provider used to fill the center cavity (usually air).</td>
</tr>
<tr>
<td><code>inner_placements_provider</code></td>
<td>The block provider used for scattered attachments (like crystals) on the inner wall.</td>
</tr>
<tr>
<td><code>invalid_blocks</code></td>
<td>A list of blocks that will abort generation if encountered at the origin point.</td>
</tr>
<tr>
<td><code>min_radius</code></td>
<td>The minimum radius of the inner geode cavity.</td>
</tr>
<tr>
<td><code>max_radius</code></td>
<td>The maximum radius of the geode's outermost shell.</td>
</tr>
</table>

---

### PillarFeature
**ID:** `abyssallib:pillar`

Generates a vertical column of blocks.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>min_height</code></td>
<td>The minimum guaranteed length of the pillar.</td>
</tr>
<tr>
<td><code>max_height</code></td>
<td>The maximum possible length of the pillar.</td>
</tr>
<tr>
<td><code>upward</code></td>
<td>Set to <code>true</code> to generate upwards from the origin, or <code>false</code> to generate downwards.</td>
</tr>
<tr>
<td><code>stop_on_invalid</code></td>
<td>Set to <code>true</code> to halt generation if an invalid block is encountered.</td>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider supplying the blocks to build the pillar.</td>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of valid blocks that the pillar is allowed to overwrite.</td>
</tr>
</table>

---

### RandomFeature
**ID:** `abyssallib:random_selector`

Evaluates a weighted list of features and places one at random.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>features</code></td>
<td>A list of feature and weight pairs.</td>
</tr>
<tr>
<td><code>default_feature</code></td>
<td>The fallback feature executed if the randomly selected feature fails to place.</td>
</tr>
</table>

---

### SimpleRandomFeature
**ID:** `abyssallib:simple_random_selector`

Places one feature chosen uniformly at random from a provided list.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>features</code></td>
<td>A list of valid features to choose from.</td>
</tr>
</table>

---

### RandomBooleanFeature
**ID:** `abyssallib:random_boolean_selector`

Places one of two specified features based on a 50% probability.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>feature_true</code></td>
<td>The feature generated if the boolean evaluates to true.</td>
</tr>
<tr>
<td><code>feature_false</code></td>
<td>The feature generated if the boolean evaluates to false.</td>
</tr>
</table>

---

### VegetationPatchFeature
**ID:** `abyssallib:vegetation_patch`

Replaces ground blocks within a specific radius and scatters vegetation on top of the newly placed ground.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>ground_provider</code></td>
<td>The block state provider defining the replacement ground terrain.</td>
</tr>
<tr>
<td><code>vegetation_provider</code></td>
<td>The block state provider defining the plants scattered on top.</td>
</tr>
<tr>
<td><code>radius</code></td>
<td>The horizontal radius of the terrain patch.</td>
</tr>
<tr>
<td><code>depth</code></td>
<td>The vertical depth to which the ground blocks will be replaced.</td>
</tr>
<tr>
<td><code>vegetation_chance</code></td>
<td>The probability (e.g., a 1-in-X chance) of placing vegetation on any given column in the patch.</td>
</tr>
<tr>
<td><code>replaceable_ground</code></td>
<td>A list of blocks that are permitted to be converted into the new ground provider.</td>
</tr>
</table>

---

### WaterloggedVegetationFeature
**ID:** `abyssallib:waterlogged_vegetation`

Scatters aquatic vegetation blocks within a specified radius, restricted exclusively to liquid columns.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>tries</code></td>
<td>The maximum number of placement attempts within the scatter radius.</td>
</tr>
<tr>
<td><code>xz_spread</code></td>
<td>The maximum horizontal distance from the origin on the X and Z axes.</td>
</tr>
<tr>
<td><code>y_spread</code></td>
<td>The maximum vertical distance from the origin on the Y axis.</td>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider supplying the aquatic vegetation.</td>
</tr>
</table>

---

### MultifaceGrowthFeature
**ID:** `abyssallib:multiface_growth`

Generates blocks that attach to multiple faces of adjacent blocks, such as glowing lichen or vines.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider representing the growth to place.</td>
</tr>
<tr>
<td><code>can_place_on</code></td>
<td>A list of valid support blocks that the growth is allowed to cling to.</td>
</tr>
</table>

---

### BlockAttachedFeature
**ID:** `abyssallib:block_attached`

Generates a single block attached to a specific face of an existing, valid supporting block.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider supplying the block to be placed.</td>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of valid blocks that can be overwritten at the origin.</td>
</tr>
<tr>
<td><code>can_attach_to</code></td>
<td>A list of valid blocks that can act as structural support.</td>
</tr>
<tr>
<td><code>directions</code></td>
<td>A list of block faces (e.g., north, up, down) to check for structural support.</td>
</tr>
</table>

---

### DripstoneClusterFeature
**ID:** `abyssallib:dripstone_cluster`

Generates clusters of pointed stalactites and stalagmites.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider supplying the block used to build the cluster.</td>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of valid blocks that the cluster is allowed to overwrite.</td>
</tr>
<tr>
<td><code>radius</code></td>
<td>The maximum horizontal radius of the entire cluster.</td>
</tr>
<tr>
<td><code>max_height</code></td>
<td>The maximum vertical length for the central columns.</td>
</tr>
<tr>
<td><code>upward</code></td>
<td>Set to <code>true</code> to generate stalagmites (growing up), or <code>false</code> for stalactites (growing down).</td>
</tr>
</table>

---

### FossilFeature
**ID:** `abyssallib:fossil`

Generates a structure derived from 3D noise, commonly used for randomized organic shapes.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>state_provider</code></td>
<td>The block state provider used to construct the primary shape.</td>
</tr>
<tr>
<td><code>targets</code></td>
<td>A list of valid blocks that the fossil can overwrite.</td>
</tr>
<tr>
<td><code>radius</code></td>
<td>The bounding radius for the 3D noise evaluation.</td>
</tr>
<tr>
<td><code>noise_frequency</code></td>
<td>The frequency multiplier applied to coordinates before sampling the noise field.</td>
</tr>
<tr>
<td><code>noise_threshold</code></td>
<td>The threshold value the 3D noise must exceed to place a block.</td>
</tr>
</table>

---

### TreeFeature
**ID:** `abyssallib:tree`

Generates a tree structure using configurable algorithms for the trunk, foliage, roots, and decorations.

<table>
<tr>
<th>Parameter</th>
<th>Information</th>
</tr>
<tr>
<td><code>trunk_provider</code></td>
<td>The block state provider supplying the log blocks.</td>
</tr>
<tr>
<td><code>foliage_provider</code></td>
<td>The block state provider supplying the leaf blocks.</td>
</tr>
<tr>
<td><code>dirt_provider</code></td>
<td>The block state provider supplying the dirt/root block substrate.</td>
</tr>
<tr>
<td><code>trunk_placer</code></td>
<td>The algorithm used to define the shape and height of the trunk. [Refer to <a href="default-trunk-placers.md">Default Trunk Placers</a>]</td>
</tr>
<tr>
<td><code>foliage_placer</code></td>
<td>The algorithm used to define the shape and spread of the leaves. [Refer to <a href="default-foliage-placers.md">Default Foliage Placers</a>]</td>
</tr>
<tr>
<td><code>root_placer</code></td>
<td>The algorithm used to map subterranean roots. [Refer to <a href="default-root-placers.md">Default Root Placers</a>]</td>
</tr>
<tr>
<td><code>decorators</code></td>
<td>A list of decorators that add secondary features (e.g., vines, cocoa beans) after the main tree generates. [Refer to <a href="default-decorators.md">Default Decorators</a>]</td>
</tr>
<tr>
<td><code>base_height</code></td>
<td>The minimum guaranteed vertical height of the trunk.</td>
</tr>
<tr>
<td><code>height_rand_a</code></td>
<td>The primary random integer added to the base height.</td>
</tr>
<tr>
<td><code>height_rand_b</code></td>
<td>The secondary random integer added to the base height.</td>
</tr>
<tr>
<td><code>foliage_radius</code></td>
<td>The minimum horizontal radius of the foliage canopy.</td>
</tr>
<tr>
<td><code>foliage_radius_rand</code></td>
<td>A random integer added to the base foliage radius.</td>
</tr>
<tr>
<td><code>dirt_targets</code></td>
<td>A list of valid blocks that the tree is allowed to grow on.</td>
</tr>
</table>