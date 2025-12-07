package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.ops.YamlOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MinecraftProvider extends Provider<ItemStack> {

    public MinecraftProvider() {
        super("minecraft");
    }

    @Override
    public boolean belongs(ItemStack value) {
        if (value == null) return false;
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

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Optional<Object>> serializeData(ItemStack value) {
        Map<String, Optional<Object>> map = new HashMap<>();
        Item item = new Item(value);

        item.getComponentMap().getAllComponents().forEach(d -> {
            String id = d.getId().toString();
            Codec<Object> codec = (Codec<Object>) ExtraCodecs.DATA_COMPONENT_CODECS.get(id);
            if (codec == null) return;

            try {
                Object encoded = codec.encode(YamlOps.INSTANCE, d);
                String str = encoded == null ? "" : encoded.toString();

                map.put(id, str.isEmpty()
                        ? Optional.empty()
                        : Optional.of(str));

            } catch (Codec.CodecException e) {
                e.printStackTrace();
            }
        });

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserializeData(Map<String, Optional<Object>> data, ItemStack value) {
        for (Map.Entry<String, Optional<Object>> entry : data.entrySet()) {

            Optional<Object> optional = entry.getValue();
            if (optional.isEmpty()) continue;

            Codec<Object> codec = (Codec<Object>) ExtraCodecs.DATA_COMPONENT_CODECS.get(entry.getKey());
            if (codec == null) continue;

            try {
                Object decoded = codec.decode(YamlOps.INSTANCE, optional.get());

                if (decoded instanceof Vanilla v) {
                    v.apply(value);
                }
            } catch (Codec.CodecException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
