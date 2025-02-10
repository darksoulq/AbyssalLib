package me.darksoul.abyssalLib.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AItem {
    private final ItemStack item;

    public AItem(ItemStack baseItem, NamespacedKey modelName) {
        ItemMeta meta = baseItem.getItemMeta();
        meta.setItemModel(modelName);
        baseItem.setItemMeta(meta);
        item = baseItem;
    }

    public AItem(Material mat, NamespacedKey modelName) {
        item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(modelName);
        item.setItemMeta(meta);
    }

    public void setTranslatableName(String name) {
        ItemMeta meta = item.getItemMeta();
        String itemKey = meta.getItemModel().getKey();
        String namespace = meta.getItemModel().getNamespace();
        meta.displayName(Component.translatable("item." + namespace + itemKey));
        item.setItemMeta(meta);
    }

    public boolean isSame(AItem itemA, ItemStack itemB) {
        ItemMeta metaA = itemA.getItem().getItemMeta();
        ItemMeta metaB = itemB.getItemMeta();
        return metaA.getItemModel() == metaB.getItemModel();
    }

    public ItemStack getItem() {
        return item;
    }
}
