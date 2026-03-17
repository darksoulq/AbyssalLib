package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.Material;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final Item INVISIBLE_ITEM = ITEMS.register("invisible", Item::new);
    public static final Item BACKWARD = ITEMS.register("backward", Item::new);
    public static final Item FORWARD = ITEMS.register("forward", Item::new);
    public static final Item CLOSE = ITEMS.register("close", Item::new);
    public static final Item BACK = ITEMS.register("back", Item::new);
    public static final Item CHECKMARK = ITEMS.register("checkmark", Item::new);

    public static final Item PERMISSION = ITEMS.register("permission", Item::new);
    public static final Item PERMISSION_BUKKIT = ITEMS.register("permission_bukkit", Item::new);
    public static final Item PERMISSION_USER = ITEMS.register("perm_user", Item::new);
    public static final Item PERMISSION_GROUP = ITEMS.register("perm_group", Item::new);

    public static final Item BOUNDING_TOGGLE = ITEMS.register("bounding_toggle", Item::new);
    public static final Item NAME_STRUCTURE = ITEMS.register("name_structure", Item::new);
    public static final Item INTEGRITY = ITEMS.register("integrity", Item::new);
    public static final Item LOAD_STRUCTURE = ITEMS.register("load_structure", Item::new);
    public static final Item MIRROR = ITEMS.register("mirror", Item::new);
    public static final Item ROTATE = ITEMS.register("rotate", Item::new);
    public static final Item SAVE = ITEMS.register("save", Item::new);
    public static final Item SIZE_X = ITEMS.register("size_x", Item::new);
    public static final Item SIZE_Y = ITEMS.register("size_y", Item::new);
    public static final Item SIZE_Z = ITEMS.register("size_z", Item::new);
    public static final Item X = ITEMS.register("x", Item::new);
    public static final Item Y = ITEMS.register("y", Item::new);
    public static final Item Z = ITEMS.register("z", Item::new);
}
