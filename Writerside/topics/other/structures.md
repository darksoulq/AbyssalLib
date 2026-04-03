---
switcher-label: In
---
# Structures
<link-summary>Guide to saving, loading, and manipulating structures</link-summary>

The Structure API allows you to save and place multi-block structures exactly like vanilla Minecraft, with full support for custom blocks.

### Saving a structure
To save a structure, use the custom structure block.
<procedure>
<step>Place the structure block and open its GUI.</step>
<step>Switch the block to <b>Save</b> mode.</step>
<step>Configure the offset and scale using the UI to cover your intended build area.</step>
<step>
Set the <tooltip term="identifier">Identifier</tooltip> using the ID button.

<img src="structure_1.png" alt="Structure block GUI setup" style="block"/>
</step>
<step>
Once the configuration is complete, build your structure within the defined bounds. Re-open the GUI and press the save button.

<img src="structure_2.png" alt="Saving the structure" width="517" height="377" style="block"/>

This saves the structure as a JSON file located at:
<path>plugins/AbyssalLib/structures/&lt;namespace&gt;/&lt;path&gt;.json</path>
</step>
</procedure>

---

### Loading and placing a structure
Structures can be loaded and placed either manually in-game or programmatically via code.

#### Loading in-game {switcher-key="Game"}
To place a structure in-game:
<procedure>
<step>Place a structure block and set it to <b>Load</b> mode.</step>
<step>Input the exact ID of the structure you saved previously.</step>
<step>
Configure the offset, rotation, mirror, and integrity settings as needed.

<img src="structure_3.png" alt="Configuring load mode" style="block"/>
</step>
<step>
Click the load button to place the structure into the world.

<img src="structure_4.png" alt="Placed structure" width="517" height="377" style="block"/>
</step>
</procedure>

#### Loading via code {switcher-key="Code"}
To load a structure programmatically, first move the generated JSON file from the server's plugins folder into your plugin's <path>resources/</path> directory.

Next, load the object using `StructureLoader`. You can then place it instantly or asynchronously.

```Java
// Load the structure from the resources folder
Structure structure = StructureLoader.loadResource(plugin, "path/to/structure.json");

// Place instantly on the main thread
structure.place(location, StructureRotation.NONE, Mirror.NONE, 1.0f);

// OR: Place asynchronously over multiple ticks to prevent lag spikes
// The last parameter dictates how many blocks are processed per tick
structure.placeAsync(plugin, location, StructureRotation.NONE, Mirror.NONE, 1.0f, 50); 
```

---

### Applying structure processors
Processors allow you to modify a structure dynamically as it is being placed. The API includes two default processors:

<table>
<tr>
<th>Name</th>
<th>ID</th>
<th>Parameters</th>
</tr>
<tr>
<td><code>IntegrityProcessor</code></td>
<td><code>integrity</code></td>
<td><code>integrity</code>: A float (0.0 to 1.0) determining how much of the structure successfully places.</td>
</tr>
<tr>
<td><code>BlockIgnoreProcessor</code></td>
<td><code>block_ignore</code></td>
<td><code>blocks</code>: A list of block IDs (e.g., <code>"minecraft:stone"</code>) to exclude from placement.</td>
</tr>
</table>

#### Applying processors in-game {switcher-key="Game"}
To apply processors to an in-game structure, you must manually edit the generated JSON file to include a `processors` array.

```JSON
{
  "size": [ 7, 8, 7 ],
  "processors": [
    {
      "type": "abyssallib:block_ignore",
      "blocks": [
        "minecraft:stone"
      ]
    }
  ],
  "palette": [ 
    {
      "Name": "minecraft:oak_log",
      "Properties": {
        "axis": "Y"
      }
    }
  ],
  "blocks": [ 
    {
      "pos": [ 6, 7, 6 ],
      "state": 0
    }
  ]
}
```

#### Applying processors via code {switcher-key="Code"}
To apply processors programmatically, use the `addProcessor` method on your loaded `Structure` instance before calling `place()`.

```Java
Structure structure = StructureLoader.loadResource(plugin, "path/to/structure.json");

// Add a processor to ignore all stone blocks during placement
structure.addProcessor(new BlockIgnoreProcessor(List.of("minecraft:stone")));

structure.place(location, StructureRotation.NONE, Mirror.NONE, 1.0f);
```