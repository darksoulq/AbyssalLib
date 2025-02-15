package me.darksoul.abyssalLib.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class AItem {
    private static final Map<NamespacedKey, AItem> itemsRegistry = new HashMap<>();

    private final ItemStack item;

    public AItem(Material mat, NamespacedKey id) {
        // Item setup
        item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(id);
        meta.displayName(Component.translatable("item." + id.getNamespace() + "." + id.getKey()));
        item.setItemMeta(meta);
        setComponents();
    }

    public abstract void setComponents();
    public ItemStack getItem() {
        return item;
    }
    public void setStackSize(int maxSize) {
        item.setData(DataComponentTypes.MAX_STACK_SIZE, maxSize);
    }

    public static boolean isSame(AItem itemA, ItemStack itemB) {
        ItemMeta metaA = itemA.getItem().getItemMeta();
        ItemMeta metaB = itemB.getItemMeta();

        return itemA.getItem().getType() == itemB.getType() &&
                metaA.getItemModel() == metaB.getItemModel();
    }
    public static AItem getAItem(NamespacedKey id) {
        return itemsRegistry.get(id);
    }
    public static Map<NamespacedKey, AItem> getItemsRegistry() {
        return itemsRegistry;
    }
    public static List<String> getItemIDsAsString() {
        Set<NamespacedKey> nKeysList = itemsRegistry.keySet();
        List<String> IDs = new ArrayList<>();
        nKeysList.forEach((key) -> {
            IDs.add(key.toString());
        });
        Collections.sort(IDs);
        return IDs;
    }
}
