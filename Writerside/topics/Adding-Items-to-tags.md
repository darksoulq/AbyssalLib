# Adding Items/Blocks to tags

> Tag files use .yml format.

AbyssalLib allows adding any item or block to an existing tag easily via YML files, to begin navigate to `/plugins/AbyssalLib/tags/[items/blocks]`.
Here, unlike recipes you must follow below steps:

- make the namespace folder (part of tag id that you are adding content to, e.g `some_plugin_id`).
- inside said folder, make a file with the name of the tag you are adding to (right part of tag id (after `:`)).
- Follow the given format for the file:
```YAML
values:
  - "minecraft:apple"
  - "#other_plugin:some_tag"
```

> As you may have noticed, you can add all contents of another tag (of the same type) to a tag by `#{tag_id}`.