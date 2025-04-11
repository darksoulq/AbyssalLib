package me.darksoul.abyssalLib.registry;

import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.mod.ModRegistry;
import me.darksoul.abyssalLib.tags.TagRegistry;

public class BuiltinRegistries {
    public static final ModRegistry MODS = new ModRegistry();
    public static final Registry<Item> ITEMS = new Registry<>();

    public static final TagRegistry<Item> ITEM_TAGS = new TagRegistry<>();
}
