package com.github.darksoulq.abyssallib.world.item.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ComponentMap {
    private final Map<DataComponentType<?>, DataComponent<?>> components = new HashMap<>();
    private final Item item;
    private final CustomEntity<? extends LivingEntity> entity;

    public ComponentMap(ItemStack stack) {
        this.item = new Item(stack);
        this.entity = null;
        load();
    }

    public ComponentMap(Item item) {
        this.item = item;
        this.entity = null;
        load();
    }

    public ComponentMap(CustomEntity<? extends LivingEntity> entity) {
        this.item = null;
        this.entity = entity;
        load();
    }

    public void load() {
        if (this.item != null) loadItem();
        if (this.entity != null) loadEntity();
    }

    @SuppressWarnings("UnstableApiUsage")
    public void loadItem() {
        if (item == null || item.getStack() == null) return;

        for (io.papermc.paper.datacomponent.DataComponentType type : item.getStack().getDataTypes()) {
            DataComponentType<?> custom = Registries.DATA_COMPONENT_TYPES.get(type.key().toString());
            if (custom == null) continue;

            if (type instanceof io.papermc.paper.datacomponent.DataComponentType.Valued<?> vl) {
                Object val = item.getStack().getData(vl);
                if (val != null) {
                    DataComponent<?> component = custom.createFromValue(val);
                    if (component != null) {
                        components.put(custom, component);
                    }
                }
            } else if (type instanceof io.papermc.paper.datacomponent.DataComponentType.NonValued nv) {
                DataComponent<?> component = custom.createFromValue(null);
                if (component != null) {
                    components.put(custom, component);
                }
            }
        }

        loadCustomComponents(item.getCTag());
    }

    public void loadEntity() {
        loadCustomComponents(entity.getCTag());
    }

    public void setData(DataComponent<?> component) {
        components.put(component.getType(), component);
        applyData();
    }

    public void removeData(DataComponentType<?> type) {
        if (components.containsKey(type)) {
            DataComponent<?> component = components.remove(type);
            if (component instanceof Vanilla v && item != null) {
                v.remove(item.getStack());
            } else {
                removeCustomComponent(type);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        return (C) components.get(type);
    }

    public boolean hasData(DataComponentType<?> type) {
        return components.containsKey(type);
    }

    public void applyData() {
        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();
        CompoundTag tag = rootTag.getCompoundOrEmpty("CustomComponents");
        CTag data = new CTag(tag);
        data.clear();

        for (Map.Entry<DataComponentType<?>, DataComponent<?>> entry : components.entrySet()) {
            if (entry.getValue() instanceof Vanilla v && item != null) {
                v.apply(item.getStack());
            } else {
                String id = Registries.DATA_COMPONENT_TYPES.getId(entry.getKey());
                if (id != null) {
                    JsonNode json = encodeComponent(entry.getValue(), JsonOps.INSTANCE);
                    data.set(id, json.toString());
                }
            }
        }
        rootTag.put("CustomComponents", data.toVanilla());
        if (item != null) item.setCTag(root);
        if (entity != null) entity.setCTag(root);
    }

    public List<DataComponent<?>> getAllComponents() {
        return new ArrayList<>(components.values());
    }

    private void removeCustomComponent(DataComponentType<?> type) {
        String id = Registries.DATA_COMPONENT_TYPES.getId(type);
        if (id == null) return;

        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();
        CompoundTag tag = rootTag.getCompoundOrEmpty("CustomComponents");
        if (tag.contains(id)) tag.remove(id);
        rootTag.put("CustomComponents", tag);
        if (item != null) item.setCTag(root);
        if (entity != null) entity.setCTag(root);
    }

    public static  <T, D> D encodeComponent(DataComponent<T> component, DynamicOps<D> ops) {
        return Try.of(() -> {
            @SuppressWarnings("unchecked")
            DataComponentType<DataComponent<T>> type = (DataComponentType<DataComponent<T>>) component.getType();
            return type.codec().encode(ops, component);
        }).get();
    }

    private void loadCustomComponents(CTag root) {
        CompoundTag tag = root.toVanilla().getCompoundOrEmpty("CustomComponents");
        if (tag.isEmpty()) return;

        for (String id : tag.keySet()) {
            DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(id);
            if (type == null) continue;

            Optional<String> encoded = tag.getString(id);
            if (encoded.isEmpty()) continue;

            Try.of(() -> {
                JsonNode json = new ObjectMapper().readTree(encoded.get());
                return type.codec().decode(JsonOps.INSTANCE, json);
            }).onSuccess(decoded -> {
                if (decoded != null) components.put(type, decoded);
            }).onFailure(t ->
                AbyssalLib.getInstance().getLogger().severe("Failed to load component " + id + ": " + t.getMessage())
            );
        }
    }
}