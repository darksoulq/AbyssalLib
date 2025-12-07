package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomMarker;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class AbyssalLibProvider extends Provider<ItemStack> {
    public AbyssalLibProvider() {
        super("abyssallib");
    }

    @Override
    public boolean belongs(ItemStack value) {
        return Item.resolve(value) != null;
    }

    @Override
    public Identifier getId(ItemStack value) {
        Item item = Item.resolve(value);
        if (item == null) return null;
        return (Identifier) item.getData(CustomMarker.class).value;
    }

    @Override
    public ItemStack get(Identifier id) {
        Item item = Registries.ITEMS.get(id.toString());
        if (item == null) return null;
        return item.getStack().clone();
    }

    @Override
    public Map<String, Optional<Object>> serializeData(ItemStack value) {
        return new MinecraftProvider().serializeData(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserializeData(Map<String, Optional<Object>> data, ItemStack value) {
        Item item = Item.resolve(value);
        if (item == null) return;
        for (Map.Entry<String, Optional<Object>> entry : data.entrySet()) {
            Optional<Object> optional = entry.getValue();
            if (optional.isEmpty()) continue;
            Codec<Object> codec = (Codec<Object>) ExtraCodecs.DATA_COMPONENT_CODECS.get(entry.getKey());
            if (codec == null) continue;
            try {
                Object decoded = codec.decode(YamlOps.INSTANCE, optional.get());
                if (decoded instanceof DataComponent<?> comp) {
                    item.setData(comp);
                }
            } catch (Codec.CodecException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
