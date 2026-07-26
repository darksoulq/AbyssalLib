package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.NbtOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomData;
import com.github.darksoulq.abyssallib.world.util.CTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores and manages {@link DataComponent}s for an item or entity.
 * <p>
 * Components are loaded lazily from Minecraft's data components or custom NBT,
 * then cached in memory. Changes can be written back to the underlying item or
 * entity using {@link #applyData()}.
 */
@SuppressWarnings("UnstableApiUsage")
public class ComponentMap {
    /**
     * The internal storage mapping component types to their active instances.
     */
    private final Map<DataComponentType<?>, DataComponent<?>> components = new HashMap<>();

    /**
     * Pending additions, modifications, or removals (where value is null).
     */
    private final Map<DataComponentType<?>, DataComponent<?>> changes = new HashMap<>();

    /**
     * A set tracking which component types have already been lazily loaded.
     */
    private final Set<DataComponentType<?>> loadedTypes = new HashSet<>();

    /**
     * Flag indicating whether all components have been fully loaded into memory.
     */
    private boolean isFullyLoaded = false;

    /**
     * The item instance associated with this map, or null if assigned to an entity.
     */
    private final Item item;

    /**
     * The custom entity instance associated with this map, or null if assigned to an item.
     */
    private final CustomEntity<? extends LivingEntity> entity;

    /**
     * Constructs a ComponentMap by wrapping a Bukkit ItemStack.
     *
     * @param stack The {@link ItemStack} to load components from.
     */
    public ComponentMap(ItemStack stack) {
        this.item = new Item(stack);
        this.entity = null;
    }

    /**
     * Constructs a ComponentMap for a specific custom Item.
     *
     * @param item The {@link Item} instance.
     */
    public ComponentMap(Item item) {
        this.item = item;
        this.entity = null;
    }

    /**
     * Constructs a ComponentMap for a specific custom Entity.
     *
     * @param entity The {@link CustomEntity} instance.
     */
    public ComponentMap(CustomEntity<? extends LivingEntity> entity) {
        this.item = null;
        this.entity = entity;
    }

    /**
     * Loads all components if they have not already been loaded.
     */
    public void load() {
        if (isFullyLoaded) return;
        isFullyLoaded = true;
        if (this.item != null) loadItem();
        if (this.entity != null) loadEntity();
    }

    /**
     * Loads all components from the associated item.
     * <p>
     * Vanilla components are read from Paper's data component API, while custom
     * components are loaded from the {@code CustomComponents} NBT tag.
     */
    public void loadItem() {
        if (item == null || item.getStack() == null) return;

        components.values().removeIf(c -> c instanceof Vanilla);

        for (io.papermc.paper.datacomponent.DataComponentType pt : item.getStack().getDataTypes()) {
            if (pt.key().asString().equals("minecraft:custom_data")) continue;

            DataComponentType<?> custom = Registries.DATA_COMPONENT_TYPES.get(pt.key().asString());
            if (custom == null) {
                custom = Registries.DATA_COMPONENT_TYPES.get(pt.key().value());
            }
            if (custom == null) continue;

            if (loadedTypes.add(custom)) {
                loadPaperComponent(pt, custom);
            }
        }

        if (loadedTypes.add(CustomData.TYPE)) {
            loadNmsCustomData();
        }

        loadAllCustomComponents(item.getCTag());
        isFullyLoaded = true;
    }

    /**
     * Loads all custom components from the associated entity.
     */
    public void loadEntity() {
        loadAllCustomComponents(entity.getCTag());
        isFullyLoaded = true;
    }

    /**
     * Loads a component if it has not already been loaded or modified.
     *
     * @param type the component type to load
     */
    private void loadType(DataComponentType<?> type) {
        if (isFullyLoaded || changes.containsKey(type) || !loadedTypes.add(type)) return;

        if (item != null) {
            if (type == CustomData.TYPE) {
                loadNmsCustomData();
                return;
            }

            String id = Registries.DATA_COMPONENT_TYPES.getId(type);
            if (id != null) {
                for (io.papermc.paper.datacomponent.DataComponentType pt : item.getStack().getDataTypes()) {
                    if (pt.key().asString().equals("minecraft:custom_data")) continue;
                    if (pt.key().asString().equals(id) || pt.key().value().equals(id)) {
                        loadPaperComponent(pt, type);
                        return;
                    }
                }
            }
            loadCustomComponent(type, item.getCTag());
        } else if (entity != null) {
            loadCustomComponent(type, entity.getCTag());
        }
    }

    /**
     * Creates a library component from a Paper data component.
     *
     * @param pt the Paper component type
     * @param targetType the corresponding library component type
     */
    private void loadPaperComponent(io.papermc.paper.datacomponent.DataComponentType pt, DataComponentType<?> targetType) {
        if (pt instanceof io.papermc.paper.datacomponent.DataComponentType.Valued<?> vl) {
            Object val = item.getStack().getData(vl);
            if (val != null) {
                DataComponent<?> component = targetType.createFromValue(val);
                if (component != null) {
                    components.put(targetType, component);
                }
            }
        } else if (pt instanceof io.papermc.paper.datacomponent.DataComponentType.NonValued) {
            DataComponent<?> component = targetType.createFromValue(null);
            if (component != null) {
                components.put(targetType, component);
            }
        }
    }

    /**
     * Loads the item's vanilla {@code minecraft:custom_data} component.
     */
    private void loadNmsCustomData() {
        if (item == null || item.getStack() == null) return;

        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item.getStack());
        net.minecraft.world.item.component.CustomData dta = nms.get(DataComponents.CUSTOM_DATA);

        if (dta != null && !dta.isEmpty()) {
            CompoundTag tag = dta.copyTag();
            tag.remove("CustomData");
            if (!tag.isEmpty()) {
                components.put(CustomData.TYPE, new CustomData(tag));
            }
        }
    }

    /**
     * Loads a single custom component from the {@code CustomComponents} tag.
     *
     * @param type the component type
     * @param root the root tag
     */
    private void loadCustomComponent(DataComponentType<?> type, CTag root) {
        String id = Registries.DATA_COMPONENT_TYPES.getId(type);
        if (id == null) return;

        CompoundTag rootTag = root.toVanilla();
        rootTag.asCompound().flatMap(c -> c.getCompound("CustomComponents")).ifPresent(customTag -> {
            Tag nbtData = customTag.get(id);
            if (nbtData != null) {
                loadAndStoreCustomComponent(id, type, nbtData);
            }
        });
    }

    /**
     * Loads every custom component stored in the {@code CustomComponents} tag.
     *
     * @param root the root tag
     */
    private void loadAllCustomComponents(CTag root) {
        CompoundTag rootTag = root.toVanilla();

        rootTag.asCompound().flatMap(c -> c.getCompound("CustomComponents")).ifPresent(customTag -> {
            for (String id : customTag.keySet()) {
                DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(id);
                if (type == null || !loadedTypes.add(type)) continue;

                Tag nbtData = customTag.get(id);
                if (nbtData == null) continue;

                loadAndStoreCustomComponent(id, type, nbtData);
            }
        });
    }

    /**
     * Decodes a serialized component and stores it in this map.
     *
     * @param id the component registry ID
     * @param type the component type
     * @param nbtData the serialized component data
     */
    private void loadAndStoreCustomComponent(String id, DataComponentType<?> type, Tag nbtData) {
        try {
            DataResult<?> result = type.codec().decode(NbtOps.INSTANCE, nbtData);
            if (result.isError()) {
                AbyssalLib.getInstance().getLogger().severe("Failed to load component " + id + ": " + result.error().get());
                return;
            }
            Object decoded = result.getOrThrow();
            if (decoded != null) {
                components.put(type, (DataComponent<?>) decoded);
            }
        } catch (Exception e) {
            AbyssalLib.getInstance().getLogger().severe("Failed to load component " + id + ": " + e.getMessage());
        }
    }

    /**
     * Adds or replaces a component and applies the change immediately.
     *
     * @param component the component to store
     */
    public void setData(DataComponent<?> component) {
        DataComponentType<?> type = component.getType();
        changes.put(type, component);
        applyData();
    }

    /**
     * Removes a component and updates the underlying item or entity.
     *
     * @param type the component type
     */
    public void removeData(DataComponentType<?> type) {
        changes.put(type, null);
        applyData();
    }

    /**
     * Returns the component of the given type.
     *
     * @param type the component type
     * @return the component, or {@code null} if it is not present
     */
    @SuppressWarnings("unchecked")
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        if (changes.containsKey(type)) {
            return (C) changes.get(type);
        }
        loadType(type);
        return (C) components.get(type);
    }

    /**
     * Returns whether this map contains the given component type.
     *
     * @param type the component type
     * @return {@code true} if the component exists
     */
    public boolean hasData(DataComponentType<?> type) {
        if (changes.containsKey(type)) {
            return changes.get(type) != null;
        }
        loadType(type);
        return components.containsKey(type);
    }

    /**
     * Writes all components back to the associated item or entity.
     * <p>
     * Vanilla components are applied through Paper's API, while custom components
     * are serialized into the {@code CustomComponents} NBT tag.
     */
    public void applyData() {
        if (changes.isEmpty()) return;

        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();

        CompoundTag customTag = rootTag.asCompound()
            .flatMap(c -> c.getCompound("CustomComponents"))
            .orElseGet(CompoundTag::new);

        boolean nbtModified = false;

        for (Map.Entry<DataComponentType<?>, DataComponent<?>> entry : changes.entrySet()) {
            DataComponentType<?> type = entry.getKey();
            DataComponent<?> comp = entry.getValue();

            if (comp != null) {
                components.put(type, comp);
                if (comp instanceof Vanilla v && item != null) {
                    v.apply(item.getStack());
                } else {
                    String id = Registries.DATA_COMPONENT_TYPES.getId(type);
                    if (id != null) {
                        Tag nbt = encodeComponent(comp, NbtOps.INSTANCE);
                        customTag.put(id, nbt);
                        nbtModified = true;
                    }
                }
            } else {
                DataComponent<?> removed = components.remove(type);
                if (removed instanceof Vanilla v && item != null) {
                    v.remove(item.getStack());
                }
                String id = Registries.DATA_COMPONENT_TYPES.getId(type);
                if (id != null && customTag.contains(id)) {
                    customTag.remove(id);
                    nbtModified = true;
                }
            }
        }

        changes.clear();

        if (nbtModified) {
            if (!customTag.isEmpty()) {
                rootTag.put("CustomComponents", customTag);
            } else {
                rootTag.remove("CustomComponents");
            }

            if (item != null) item.setCTag(root);
            if (entity != null) entity.setCTag(root);
        }
    }

    /**
     * Returns every loaded component.
     *
     * @return all components in this map
     */
    public List<DataComponent<?>> getAllComponents() {
        load();
        Map<DataComponentType<?>, DataComponent<?>> effective = new HashMap<>(components);
        for (Map.Entry<DataComponentType<?>, DataComponent<?>> entry : changes.entrySet()) {
            if (entry.getValue() == null) {
                effective.remove(entry.getKey());
            } else {
                effective.put(entry.getKey(), entry.getValue());
            }
        }
        return new ArrayList<>(effective.values());
    }

    /**
     * Encodes a component using the given {@link DynamicOps}.
     *
     * @param component the component to encode
     * @param ops the target serialization format
     * @return the encoded value
     * @param <T> the component value type
     * @param <D> the serialized data type
     */
    public static <T, D> D encodeComponent(DataComponent<T> component, DynamicOps<D> ops) {
        @SuppressWarnings("unchecked")
        DataComponentType<DataComponent<T>> type = (DataComponentType<DataComponent<T>>) component.getType();
        DataResult<D> result = type.codec().encode(ops, component);
        if (result.isError()) {
            throw new RuntimeException("Serialization failure: " + result.error().get());
        }
        return result.getOrThrow();
    }
}