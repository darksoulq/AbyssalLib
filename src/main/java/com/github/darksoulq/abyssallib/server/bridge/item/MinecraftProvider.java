package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.ItemProvider;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.github.darksoulq.abyssallib.world.item.component.ComponentMap.encodeComponent;

public class MinecraftProvider extends ItemProvider {

    public MinecraftProvider() {
        super("minecraft");
    }

    @Override
    public boolean belongs(ItemStack value) {
        return true;
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
    public Map<String, Optional<Object>> serializeData(ItemStack value, DynamicOps<?> ops) {
        Map<String, Optional<Object>> map = new HashMap<>();
        Item item = new Item(value);
        Item vanilla = new Item(new ItemStack(value.getType()));

        item.getComponentMap().getAllComponents().forEach(component -> {
            DataComponent<?> vComp = vanilla.getData(component.getType());
            if (vComp != null && Objects.equals(component.getValue(), vComp.getValue())) return;
            Object encoded = encodeComponent(component, ops);
            map.put(Registries.DATA_COMPONENT_TYPES.getId(component.getType()), Optional.ofNullable(encoded));
        });

        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D> void deserializeData(Map<String, Optional<D>> data, ItemStack value, DynamicOps<D> ops) {
        for (Map.Entry<String, Optional<D>> entry : data.entrySet()) {

            Optional<D> optional = entry.getValue();
            if (optional.isEmpty()) continue;

            Codec<Object> codec = (Codec<Object>) ExtraCodecs.DATA_COMPONENT_CODECS.get(entry.getKey());
            if (codec == null) continue;

            try {
                Object decoded = codec.decode(ops, optional.get());

                if (decoded instanceof Vanilla v) {
                    v.apply(value);
                }
            } catch (Codec.CodecException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
