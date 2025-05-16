package com.github.darksoulq.abyssallib.tag;

import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.registry.BuiltinRegistries;
import com.github.darksoulq.abyssallib.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.registry.object.DeferredObject;

public class BuiltinTags {
    public static DeferredRegistry<ItemTag> TAGS = DeferredRegistry.create(BuiltinRegistries.ITEM_TAGS, "abyssallib");

    public static DeferredObject<ItemTag> AXES = TAGS.register("axes", (name, id) -> new ItemTag("axes"));
    public static DeferredObject<ItemTag> PICKAXES = TAGS.register("pickaxes", (name, id) -> new ItemTag("pickaxes"));
    public static DeferredObject<ItemTag> SHOVELS = TAGS.register("shovels", (name, id) -> new ItemTag("pickaxes"));
    public static DeferredObject<ItemTag> HOES = TAGS.register("hoes", (name, id) -> new ItemTag("hoes"));
    public static DeferredObject<ItemTag> SWORDS = TAGS.register("swords", (name, id) -> new ItemTag("swords"));
}
