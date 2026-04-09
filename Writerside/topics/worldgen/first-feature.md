# Creating Your First Feature
<link-summary>Guide to generating custom world features</link-summary>

Features allow you to generate blocks naturally into the world during chunk generation, such as custom trees, ores, or structures.

### Creating a feature
To create a new feature, navigate to your server's plugin directory and create a JSON file at the following path:
<path>plugins/AbyssalLib/worldgen/features/&lt;namespace&gt;/&lt;feature_name&gt;.json</path>

*(For example: `plugins/AbyssalLib/worldgen/features/abyssallib_example/gold_block_ore.json`)*

The feature JSON file consists of four primary sections:

1. `"type"`: Defines the base feature logic to use (e.g., an ore vein or a tree).
2. `"config"`: Holds the specific configuration parameters required by the chosen feature type.
3. `"placement"`: Defines the rules and modifiers that dictate where, how often, and at what altitudes the feature generates within a chunk.
4. `"worlds"`: A list of world names where this feature is permitted to generate. If this array is empty, the feature will not generate anywhere.

```JSON
{
  "type": "abyssallib:ore",
  "config": {
    "targets": [
      {
        "target": [
          {
            "id": "minecraft:stone"
          },
          {
            "id": "minecraft:deepslate"
          }
        ],
        "state_provider": {
          "type": "abyssallib:simple",
          "state": {
            "id": "minecraft:gold_block"
          }
        }
      }
    ],
    "size": 9
  },
  "placement": [
    {
      "type": "abyssallib:count",
      "count": 12
    },
    {
      "type": "abyssallib:in_square"
    },
    {
      "type": "abyssallib:height_range",
      "min_inclusive": -64,
      "max_inclusive": 32
    }
  ],
  "worlds": [
    "world"
  ]
}
```

<note>
For comprehensive lists of available generation options, refer to the following resources:
<list>
<li><a href="default-features.md"/></li>
<li><a href="default-placement-modifiers.md"/></li>
<li><a href="default-state-providers.md"/></li>
</list>
</note>

---

### BlockInfo Format
When defining blocks inside your configuration (such as the `"target"` blocks to replace, or the `"state"` block to place), AbyssalLib uses a specific `BlockInfo` JSON object format.

<table>
<tr>
<th>Key</th>
<th>Information</th>
</tr>
<tr>
<td><code>"id"</code></td>
<td>The namespace identifier of the block (e.g., <code>"minecraft:stone"</code>, <code>"abyssallib_example:custom_ore"</code>).</td>
</tr>
<tr>
<td><code>"states"</code></td>
<td>A key-value map defining standard vanilla block states (e.g., <code>"facing": "north"</code>).</td>
</tr>
<tr>
<td><code>"properties"</code></td>
<td>A key-value map defining custom properties specific to AbyssalLib custom blocks.</td>
</tr>
<tr>
<td><code>"nbt"</code></td>
<td>A JSON object defining TileEntity data for vanilla blocks.</td>
</tr>
</table>