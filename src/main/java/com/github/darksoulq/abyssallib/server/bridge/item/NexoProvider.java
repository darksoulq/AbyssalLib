package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class NexoProvider extends Provider<ItemStack> {
    public NexoProvider() {
        super("nexo");
    }

    @Override
    public boolean belongs(ItemStack value) {
        return NexoItems.exists(value);
    }

    @Override
    public Identifier getId(ItemStack value) {
        String id = NexoItems.idFromItem(value);
        return id == null ? null : Identifier.of(id);
    }

    @Override
    public ItemStack get(Identifier id) {
        ItemBuilder builder = NexoItems.itemFromId(id.toString());
        return builder == null ? null : builder.build();
    }

    @Override
    public Map<String, Optional<Object>> serializeData(ItemStack value, DynamicOps<?> ops) {
        return new MinecraftProvider().serializeData(value, ops);
    }

    @Override
    public <T> void deserializeData(Map<String, Optional<T>> data, ItemStack value, DynamicOps<T> ops) {
        new MinecraftProvider().deserializeData(data, value, ops);
    }
}
