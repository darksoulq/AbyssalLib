---
switcher-label: In
---
# Creating Your First Feature
<secondary-label ref="wip"/>

Features allow you to generate blocks into the world naturally, like trees, ores, structures and so on.

### Creating a feature {switcher-key=JSON id="creating-a-feature_json"}
To create a feature, navigate to <path>plugins/AbyssalLib/worldgen/features/</path> then make a namespace folder (used for the namespace part of feature ID)
next make a JSON file with the name of the feature. (e.g gold_block_ore.json)

The JSON file consists of three parts:
1. "type": this defines what feature type to use.
2. "config": this is used to configure the feature.
3. "worlds": this defines which worlds the feature should apply to, if it is empty, the feature wont be applied to any world.

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
          "type": "simple",
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
      "type": "count",
      "count": 12
    },
    {
      "type": "in_square"
    },
    {
      "type": "height_range",
      "min_inclusive": {
        "absolute": -64
      },
      "max_inclusive": {
        "absolute": 32
      }
    }
  ],
  "worlds": [
    "world"
  ]
}
```

The config for all default placement modifiers is present at [], for all features at [] and for all state providers at [].

### Creating a feature {switcher-key=Code id="creating-a-feature_code"}

### BlockInfo format {switcher-key=JSON}

"id" -> the identifier of the block to use ("minecraft:stone", "someplugin:someblock")
"pos" -> optional, used for tile entities like chests
"states" -> vanilla block states map (covered states available at [])
"properties" -> CustomBlock properties (only for abyssallib blocks)
