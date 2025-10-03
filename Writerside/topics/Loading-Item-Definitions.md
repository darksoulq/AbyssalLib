# Loading Item Definitions

> Item Definitions are used to load models for items.

### Loading ItemDefinitions
Item Definitions are one of the more complex assets, as you CAN autoload them, however in case you dont do that, it takes upto 3 lines to set it up.

```Java
public class MyResourcePack {
    public static void load(Plugin plugin) {
        ResourcePack pack = new ResourcePack(plugin, "plugin_id");
        Namespace ns = pack.namespace("plugin_id");
        
        tex = ns.texture("item/iron_sword");
        Model model = ns.model("item/iron_sword", true);
        /* OR
         Model model = ns.model("item/iron_sword", false)
         mod.parent("minecraft:item/generated");
         mod.texture("layer0", tex); */
        
        Selector.Model sel = new Selector.Model(model);
        ns.itemDefinition("iron_sword", sel, false);
        
        pack.register(false);
    }
}
```

> As you may have noticed, you have to load model, and construct a selector for item definition (in case you have made RPs before, you should know which selectors are available in MC, otherwise please watch a tutorial)
> You can also construct the entire model in code, including cubes, groups and so on.
> - `Namespace#itemDefinition` takes in three args:
>   - name: the name that this definition file will use (afterwards items can use it by setting item model to plugin_id:name).
>   - selector: the constructed Selector for the item definition.
>   - handAnimationOnSwap: this boolean decides whether the up-down item animation should play when swapping item between offhand and mainhand.


##### The ResourcePack API provides much more than just this, for example you can load mcmeta files, icon, pack mcmeta, waypoint styles, and much more, however not EVERYTHING is contained, some files you will have to load manually using JsonAsset as an example, report missing stuff on discord or github issues and ill be adding it all!