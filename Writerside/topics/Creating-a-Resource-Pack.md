# Creating a Resource Pack

> AbyssalLib provides an extensive resource pack API so developers can focus on features (take it with a pinch of salt as RP setup CAN get pretty long).

### Creating the Pack:
To create the ResourcePack you need to create a few objects:

```java
public class MyResourcePack {
    public static void load(Plugin plugin) {
        ResourcePack pack = new ResourcePack(plugin, "plugin_id");
        Namespace ns = pack.namespace("plugin_id");
        
        // Setup resource pack here
        
        pack.register(false);
    }
}
```

<warning>Make SURE that "plugin_id" is lowercase.</warning>
> - The boolean passed in pack.register is to decide whether the generated .zip should be overwritten if it already exists or not.
> - To make it easier for yourself, keep a static PLUGIN_ID variable in your main class.
> - Create a resourcepack/plugin_id/ folder inside your resources/ folder, this is where assets are loaded from.

### Loading your first texture:
There are two ways to load textures: load using a file inside your resources folder. OR; load using byte data.

```java
public class MyResourcePack {
    public static Texture EXAMPLE;
    
    public static void load(Plugin plugin) {
        ResourcePack pack = new ResourcePack(plugin, "plugin_id");
        Namespace ns = pack.namespace("plugin_id");
        
        EXAMPLE = ns.texture("item/iron_sword");
        // OR
        // EXAMPLE = ns.texture("item/iron_sword", data)
        
        pack.register(false);
    }
}
```

> - The texture is loaded from `resources/resourcepack/plugin_id/textures/item/iron_sword.png` and saved properly.
> - In case where you provide the byte[] data, you need not have the texture in resources/
> - In some cases you might not want to store the texture in static vars (well mostly), as you normally would just have l=to load and forget.

Next sections will cover Glyphs (font files), Lang, and Item Definitions
