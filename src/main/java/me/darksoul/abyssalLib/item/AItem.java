package me.darksoul.abyssalLib.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AItem {
    private static final Map<String, List<NamespacedKey>> itemsRegistry = new HashMap<>();

    private final ItemStack item;

    public AItem(Material mat, NamespacedKey id) {
        putInMap(id.getNamespace(), id);
        item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(id);
        meta.displayName(Component.translatable("item." + id.getNamespace() + id.getKey()));
        item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }

    public static boolean isSame(AItem itemA, ItemStack itemB) {
        ItemMeta metaA = itemA.getItem().getItemMeta();
        ItemMeta metaB = itemB.getItemMeta();

        return itemA.getItem().getType() == itemB.getType() &&
                metaA.getItemModel() == metaB.getItemModel();
    }

    private static void putInMap(String namespace, NamespacedKey key) {
        itemsRegistry.computeIfAbsent(namespace, k -> new java.util.ArrayList<>()).add(key);
    }
}
