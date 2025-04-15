# Resource Pack Generation
## How to Generate Your Resource Pack

To generate your mod's resource pack, you **must** call the following line during plugin initialization (e.g., inside `onEnable()`, preferably at end):

```java
new Resourcepack(this, MODID).generate();
```

- `this` is your main plugin class (extending `JavaPlugin`).
- `MODID` is your mod ID string (e.g., `"mymod"`).

This will:
- Automatically zip your assets into a usable resource pack.
- Prepare it for hosting or applying to clients.

## Folder Structure

Before calling `generate()`, ensure your assets are placed correctly in your resources folder:

```
src/main/resources/assets/<modid>/
```

For example:

```
src/main/resources/assets/mymod/textures/item/example_item.png
src/main/resources/assets/mymod/items/example_item.json
```

Everything inside the `<modid>` folder will be included in the generated resource pack.

## Notes

- If the pack already exists, it will not be regenerated unless you delete or overwrite it.
- Make sure your file paths and names match what Minecraft expects.

With this setup, your assets will be automatically included and ready for use!
