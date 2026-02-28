package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final Item INVISIBLE_ITEM = ITEMS.register("invisible", (id) -> new Item(id, Material.STICK));
    public static final Item BACKWARD = ITEMS.register("backward", (id) -> new Item(id, Material.STICK));
    public static final Item FORWARD = ITEMS.register("forward", (id) -> new Item(id, Material.STICK));
    public static final Item CLOSE = ITEMS.register("close", (id) -> new Item(id, Material.STICK));
    public static final Item BACK = ITEMS.register("back", (id) -> new Item(id, Material.STICK));
    public static final Item CHECKMARK = ITEMS.register("checkmark", (id) -> new Item(id, Material.STICK));

    public static final Item PERMISSION = ITEMS.register("permission", id -> new Item(id, Material.STICK));
    public static final Item PERMISSION_BUKKIT = ITEMS.register("permission_bukkit", id -> new Item(id, Material.STICK));
    public static final Item PERMISSION_USER = ITEMS.register("perm_user", id -> new Item(id, Material.STICK));
    public static final Item PERMISSION_GROUP = ITEMS.register("perm_group", id -> new Item(id, Material.STICK));

    public static final Item BOUNDING_TOGGLE = ITEMS.register("bounding_toggle", (id) -> new Item(id, Material.STICK));
    public static final Item NAME_STRUCTURE = ITEMS.register("name_structure", (id) -> new Item(id, Material.STICK));
    public static final Item INTEGRITY = ITEMS.register("integrity", (id) -> new Item(id, Material.STICK));
    public static final Item LOAD_STRUCTURE = ITEMS.register("load_structure", (id) -> new Item(id, Material.STICK));
    public static final Item MIRROR = ITEMS.register("mirror", (id) -> new Item(id, Material.STICK));
    public static final Item ROTATE = ITEMS.register("rotate", (id) -> new Item(id, Material.STICK));
    public static final Item SAVE = ITEMS.register("save", (id) -> new Item(id, Material.STICK));
    public static final Item SIZE_X = ITEMS.register("size_x", (id) -> new Item(id, Material.STICK));
    public static final Item SIZE_Y = ITEMS.register("size_y", (id) -> new Item(id, Material.STICK));
    public static final Item SIZE_Z = ITEMS.register("size_z", (id) -> new Item(id, Material.STICK));
    public static final Item X = ITEMS.register("x", (id) -> new Item(id, Material.STICK));
    public static final Item Y = ITEMS.register("y", (id) -> new Item(id, Material.STICK));
    public static final Item Z = ITEMS.register("z", (id) -> new Item(id, Material.STICK));
}
