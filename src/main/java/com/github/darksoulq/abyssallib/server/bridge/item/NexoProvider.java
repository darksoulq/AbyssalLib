package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

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
}
