# Loading lang files

> Lang files are where you define your translations!

### Creating the lang file:
To load a lang file, you have two ways, one is having a lang file prepared, the other is to populate in-code.

```Java
public class MyResourcePack {
    public static Texture EXAMPLE;
    
    public static void load(Plugin plugin) {
        ResourcePack pack = new ResourcePack(plugin, "plugin_id");
        Namespace ns = pack.namespace("plugin_id");
        
        Lang enUs = ns.lang("en_us", false);
        
        enUs.put("my.translation.key", "My Key");
        
        pack.register(false);
    }
}
```

> - The first arg of `Namespace#lang` is the file name (in this case en_us)
> - The second arg of it is whether to autoload the file, if you choose to pass in true, the lang file must be in `resourcepack/lang/name.json`
> - You can then use .put to add in translation entries