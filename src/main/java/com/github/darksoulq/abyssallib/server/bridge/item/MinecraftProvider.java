package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MinecraftProvider extends Provider<ItemStack> {

    public MinecraftProvider() {
        super("minecraft");
    }

    @Override
    public boolean belongs(ItemStack value) {
        return value.equals(ItemStack.of(value.getType()));
    }

    @Override
    public Identifier getId(ItemStack value) {
        return Identifier.of("minecraft", value.getType().name().toLowerCase(Locale.ROOT));
    }

    @Override
    public ItemStack get(Identifier id) {
        return ItemStack.of(Material.valueOf(id.getPath().toUpperCase(Locale.ROOT)));
    }
}
