package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.internal.GuiItem;

public class Items {
    public static final DeferredRegistry<Item> ITEMS = DeferredRegistry.create(Registries.ITEMS, "abyssallib");

    public static final Item INVISIBLE_ITEM = ITEMS.register("invisible", GuiItem::new);
    public static final Item BACKWARD = ITEMS.register("backward", GuiItem::new);
    public static final Item FORWARD = ITEMS.register("forward", GuiItem::new);
    public static final Item CLOSE = ITEMS.register("close", GuiItem::new);
    public static final Item BACK = ITEMS.register("back", GuiItem::new);
    public static final Item CHECKMARK = ITEMS.register("checkmark", GuiItem::new);

    public static final Item PERMISSION = ITEMS.register("permission", GuiItem::new);
    public static final Item PERMISSION_BUKKIT = ITEMS.register("permission_bukkit", GuiItem::new);
    public static final Item PERMISSION_USER = ITEMS.register("perm_user", GuiItem::new);
    public static final Item PERMISSION_GROUP = ITEMS.register("perm_group", GuiItem::new);

    public static final Item BOUNDING_TOGGLE = ITEMS.register("bounding_toggle", GuiItem::new);
    public static final Item NAME_STRUCTURE = ITEMS.register("name_structure", GuiItem::new);
    public static final Item INTEGRITY = ITEMS.register("integrity", GuiItem::new);
    public static final Item LOAD_STRUCTURE = ITEMS.register("load_structure", GuiItem::new);
    public static final Item MIRROR = ITEMS.register("mirror", GuiItem::new);
    public static final Item ROTATE = ITEMS.register("rotate", GuiItem::new);
    public static final Item SAVE = ITEMS.register("save", GuiItem::new);
    public static final Item SIZE_X = ITEMS.register("size_x", GuiItem::new);
    public static final Item SIZE_Y = ITEMS.register("size_y", GuiItem::new);
    public static final Item SIZE_Z = ITEMS.register("size_z", GuiItem::new);
    public static final Item X = ITEMS.register("x", GuiItem::new);
    public static final Item Y = ITEMS.register("y", GuiItem::new);
    public static final Item Z = ITEMS.register("z", GuiItem::new);
}
