package me.darksoul.abyssalLib.item.component;

import me.darksoul.abyssalLib.item.AItem;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ALore {
    private final ItemStack item;
    private final List<Component> lore = new ArrayList<>();

    public ALore(@NotNull AItem aItem) {
        item = aItem.getItem();
    }

    public ALore addLore(Component text) {
        lore.add(text);
        return this;
    }

    public void build() {
        item.lore(lore);
    }
}
