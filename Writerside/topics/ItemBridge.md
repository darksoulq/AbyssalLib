# ItemBridge

> ItemBridge is a compatibility layer to allow getting ItemStack from multiple different plugins using a specific format (`plugin:namespace:item_id`), or in case of vanilla `minecraft:material_name`

### Using ItemBridge
```Java
ItemStack stack = ItemBridge.get("nexo:mynamespace:my_item");
```
The above snippet attempts to get the item with id `mynamespace:my_item` from the `Nexo` plugin, in case the item isnt found it will return null.

```Java
Identifier id = ItemBridge.getId(ItemStack);
```
The above snippet attempts to get the id of the given ItemStack, in case its neither in a loaded hook or a vanilla item, it will return null.

```Java
String id = ItemBridge.getIdAsString(ItemStack);
```
The above snippet attempts to get the id of the given ItemStack, however unlike `.getId(ItemStack)`, if the item is a custom item not belonging to any hook it will return the serialized base64 encoded string of said item.

> There are also methods such as:
> - `.hasProvider(String id)`
> - `.hasProvider(ItemStack item)`
> - `.get(Identifier id)`
> - `.asString(ItemStack item)`
> - `.asAmountMap(ItemStack item)`