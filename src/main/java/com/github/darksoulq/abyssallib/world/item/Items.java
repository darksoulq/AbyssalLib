package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final Holder<Item> INVISIBLE_ITEM = ITEMS.register("invisible", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> BACKWARD = ITEMS.register("backward", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> FORWARD = ITEMS.register("forward", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> CLOSE = ITEMS.register("close", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> BACK = ITEMS.register("back", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> CHECKMARK = ITEMS.register("checkmark", (id) -> new Item(id, Material.STICK));

    public static final Holder<Item> BOUNDING_TOGGLE = ITEMS.register("bounding_toggle", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> NAME_STRUCTURE = ITEMS.register("name_structure", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> INTEGRITY = ITEMS.register("integrity", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> LOAD_STRUCTURE = ITEMS.register("load_structure", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> MIRROR = ITEMS.register("mirror", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> ROTATE = ITEMS.register("rotate", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> SAVE = ITEMS.register("save", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> SIZE_X = ITEMS.register("size_x", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> SIZE_Y = ITEMS.register("size_y", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> SIZE_Z = ITEMS.register("size_z", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> X = ITEMS.register("x", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> Y = ITEMS.register("y", (id) -> new Item(id, Material.STICK));
    public static final Holder<Item> Z = ITEMS.register("z", (id) -> new Item(id, Material.STICK));
}
