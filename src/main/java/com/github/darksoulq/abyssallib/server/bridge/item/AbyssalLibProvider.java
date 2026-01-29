package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.bridge.ItemProvider;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomMarker;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AbyssalLibProvider extends ItemProvider {
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
        if (!item.hasData(CustomMarker.TYPE)) return null;
        return item.getData(CustomMarker.TYPE).getValue();
    }

    @Override
    public ItemStack get(Identifier id) {
        Item item = Registries.ITEMS.get(id.toString());
        if (item == null) return null;
        return item.getStack().clone();
    }

    @Override
    public Map<String, Optional<Object>> serializeData(ItemStack value, DynamicOps<?> ops) {
        Map<String, Optional<Object>> map = new HashMap<>();
        Item item = Item.resolve(value);
        if (item == null) return map;
        Item base = Registries.ITEMS.get(item.getId().toString());
        if (base == null) return map;

        for (DataComponent<?> component : item.getComponentMap().getAllComponents()) {
            DataComponentType<?> type = component.getType();
            DataComponent<?> baseComp = base.getData(type);

            if (baseComp != null && Objects.equals(component.getValue(), baseComp.getValue())) continue;

            String typeId = Registries.DATA_COMPONENT_TYPES.getId(type);
            if (typeId == null) continue;

            Object encoded = ComponentMap.encodeComponent(component, ops);
            map.put(typeId, Optional.ofNullable(encoded));
        }
        return map;
    }

    @Override
    public <T> void deserializeData(Map<String, Optional<T>> data, ItemStack value, DynamicOps<T> ops) {
        Item item = Item.resolve(value);
        if (item == null) return;

        for (Map.Entry<String, Optional<T>> entry : data.entrySet()) {
            String key = entry.getKey();
            Optional<T> optValue = entry.getValue();
            if (optValue.isEmpty()) continue;

            DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(key);
            if (type == null) {
                AbyssalLib.getInstance().getLogger().warning("Unknown component type: " + key);
                continue;
            }

            Try.of(() -> type.codec().decode(ops, optValue.get()))
                .onSuccess(item::setData)
                .onFailure(t -> AbyssalLib.getInstance().getLogger().severe("Failed to decode component " + key + ": " + t.getMessage()));
        }
    }

}