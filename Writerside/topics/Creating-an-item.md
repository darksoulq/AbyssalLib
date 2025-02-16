# Creating an item

creating an item is super simple (WIP in various ways)

<procedure title="Creating the item">
<step>
Lets start of by making our class for the item (make it somewhere like "item/") and extend `me.darksoul.abyssalLib.item.AItem`.

```Java
public class MyItem extends AItem {
    public MyItem() {
        super(Material.TYPE, new NamespacedKey("namespace", "modelid"));
    }
    
    @Override
    public void setComponents() {
        // Here we set components.
    }
}
```
</step>
<step>
Now you have to register your item for it to be accessible by /abyssallib give, for this, we register it inside our MyMod (previos example) class.

```Java
public class MyMod extends AMod {
    public MyMod() {
        super(YouPluginInstance);
    }

    @Override
    public void setRegistries() {
        register(RegistryType.ITEM, new ExampleItem());
    }
}
```
</step>
<step>
Now we can load the server and run /abyssallib give "namespace:modelID", however, you will see that the texture is a purple/black box, to fix this we need to setup the texture/item definition/model.
first create a assets folder and make a folder without your namespace (in this example its "namespace"), this follows the basic resourcepack structure. now make a "items" folder, a textures/item, lang/, and models/item folder.
now your resources/ folder should contain these:

```
|_assets/
  |_namespace/
    |_textures/
    | |_item/
    |   |_texture.png
    |_models/
    | |_item/
    |   |_model.json
    |_items/
    | |_modelid.json
    |_lang/
      |_en_us.json
```

### Important {collapsible="true", default-state="expanded"}
If you didn't realize, when we passed the namespaced key to the AItem (super()), the "namespace" referred to the namespace in assets/, and "modelid" was the name of the json file that defines the item in items/ (without mjson)
</step>
<step>
after you have got the texture, you will need to define the model (model.json) for the item, an example is:

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "namespace:item/texture.png"
  }
}
```
</step>
<step>
Now you will need to make the item definition for the item (in this case items/modelis.json)

```json
{
  "model": {
    "type": "minecraft:model",
    "model": "namespace:item/model.json"
  }
}
```

Now once you load the compiled plugin on the server, and apply the generated.zip resourcepack (in AbyssalLib/pack/) you will see your item with the proper texture, however, the name would appear as something like `item.namespace.modelid`. This is because you need to provide the translation for your item name.
</step>
<step>
adding translations is very easy and all you need to do is edit the lang/en_us.json file (or other file if you are using another language).

```json
{
  "item.namespace.modelid": "My Item"
}
```

now after recompiling, launching  server, and applying the generated resourcepack, your item will have the name "My Item".
</step>
</procedure>