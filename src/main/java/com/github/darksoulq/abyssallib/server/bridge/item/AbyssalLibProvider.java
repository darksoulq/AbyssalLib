package com.github.darksoulq.abyssallib.server.bridge.item;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomMarker;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.github.darksoulq.abyssallib.world.item.component.ComponentMap.encodeComponent;

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
    public Map<String, Optional<Object>> serializeData(ItemStack value, DynamicOps<?> ops) {
        Map<String, Optional<Object>> map = new HashMap<>();
        Item item = Item.resolve(value);
        Item base = Item.resolve(value);
        if (item == null) return map;
        for (DataComponent<?> component : item.getComponentMap().getAllComponents()) {
            DataComponent<?> vComp = base.getData(component.getId());
            if (vComp != null && Objects.equals(component.value, vComp.value)) continue;
            Object encoded = encodeComponent(component, ops);
            map.put(component.getId().toString(), Optional.ofNullable(encoded));
        }
        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void deserializeData(Map<String, Optional<T>> data, ItemStack value, DynamicOps<T> ops) {
        Map<String, Optional<T>> custom = new HashMap<>();
        Item item = Item.resolve(value);
        if (item == null) return;
        for (Map.Entry<String, Optional<T>> entry : data.entrySet()) {
            if (!entry.getKey().startsWith("minecraft")) {
                custom.put(entry.getKey(), entry.getValue());
                continue;
            }
            Optional<T> optional = entry.getValue();
            if (optional.isEmpty()) continue;
            Codec<Object> codec = (Codec<Object>) ExtraCodecs.DATA_COMPONENT_CODECS.get(entry.getKey());
            if (codec == null) continue;
            try {
                Object decoded = codec.decode(ops, optional.get());
                if (decoded instanceof DataComponent<?> comp) {
                    item.setData(comp);
                }
            } catch (Codec.CodecException e) {
                throw new RuntimeException(e);
            }
        }

        for (Map.Entry<String, Optional<T>> entry : custom.entrySet()) {
            Class<? extends DataComponent<?>> cls = Registries.DATA_COMPONENTS.get(entry.getKey());
            if (cls == null) continue;

            try {
                Optional<T> optional = entry.getValue();
                if (optional.isEmpty()) continue;

                Field codecField = cls.getDeclaredField("CODEC");
                if (!Modifier.isStatic(codecField.getModifiers())) throw new RuntimeException("Missing static CODEC in " + cls.getName());
                codecField.setAccessible(true);
                Codec<?> codec = (Codec<?>) codecField.get(null);

                DataComponent<?> decoded = (DataComponent<?>) codec.decode(ops, optional.get());
                if (decoded != null) item.setData(decoded);

            } catch (NoSuchFieldException e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to find static CODEC field for custom component " + cls.getSimpleName() + ": " + e.getMessage());
            } catch (Codec.CodecException e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to decode custom component " + entry.getKey() + ": " + e.getMessage());
            } catch (Exception e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to load custom component " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }
}
