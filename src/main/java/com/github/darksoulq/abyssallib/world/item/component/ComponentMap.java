package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.NbtOps;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A specialized map used to manage and persist {@link DataComponent}s for library objects.
 * <p>
 * This class bridge the gap between volatile in-memory component states and persistent
 * storage (NBT for items/entities). It handles both standard Minecraft Vanilla components
 * via Paper's API and custom AbyssalLib components via direct NBT serialization.
 */
public class ComponentMap {
    /** The internal storage mapping component types to their active instances. */
    private final Map<DataComponentType<?>, DataComponent<?>> components = new HashMap<>();
    /** The item instance associated with this map, or null if assigned to an entity. */
    private final Item item;
    /** The custom entity instance associated with this map, or null if assigned to an item. */
    private final CustomEntity<? extends LivingEntity> entity;

    /**
     * Constructs a ComponentMap by wrapping a Bukkit ItemStack.
     *
     * @param stack The {@link ItemStack} to load components from.
     */
    public ComponentMap(ItemStack stack) {
        this.item = new Item(stack);
        this.entity = null;
        load();
    }

    /**
     * Constructs a ComponentMap for a specific custom Item.
     *
     * @param item The {@link Item} instance.
     */
    public ComponentMap(Item item) {
        this.item = item;
        this.entity = null;
        load();
    }

    /**
     * Constructs a ComponentMap for a specific custom Entity.
     *
     * @param entity The {@link CustomEntity} instance.
     */
    public ComponentMap(CustomEntity<? extends LivingEntity> entity) {
        this.item = null;
        this.entity = entity;
        load();
    }

    /**
     * Triggers the component discovery process for the associated item or entity.
     */
    public void load() {
        if (this.item != null) loadItem();
        if (this.entity != null) loadEntity();
    }

    /**
     * Synchronizes internal storage with the NMS/Paper data components of the associated item.
     * <p>
     * Scans all vanilla data types and maps them to library-compatible {@link DataComponent}s
     * using {@link DataComponentType#createFromValue(Object)}.
     */
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
            } else if (type instanceof io.papermc.paper.datacomponent.DataComponentType.NonValued) {
                DataComponent<?> component = custom.createFromValue(null);
                if (component != null) {
                    components.put(custom, component);
                }
            }
        }

        loadCustomComponents(item.getCTag());
    }

    /**
     * Synchronizes internal storage with the persistent tags of the associated entity.
     */
    public void loadEntity() {
        loadCustomComponents(entity.getCTag());
    }

    /**
     * Assigns a component to this map and immediately triggers an application to the source.
     *
     * @param component The {@link DataComponent} to add or update.
     */
    public void setData(DataComponent<?> component) {
        components.put(component.getType(), component);
        applyData();
    }

    /**
     * Removes a component by its type and updates the underlying item/entity state.
     *
     * @param type The {@link DataComponentType} to remove.
     */
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

    /**
     * Retrieves a component instance for the given type.
     *
     * @param <C>  The specific DataComponent class.
     * @param type The {@link DataComponentType} to retrieve.
     * @return The component instance, or {@code null} if not present.
     */
    @SuppressWarnings("unchecked")
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        return (C) components.get(type);
    }

    /**
     * Checks if a component type exists in this map.
     *
     * @param type The {@link DataComponentType} to check.
     * @return {@code true} if present.
     */
    public boolean hasData(DataComponentType<?> type) {
        return components.containsKey(type);
    }

    /**
     * Serializes all current components back into the source's persistent data (PDC/NBT).
     * <p>
     * Custom components are encoded directly into NBT tags and stored under the
     * {@code CustomComponents} compound tag. Vanilla components are applied
     * directly via {@link Vanilla#apply(ItemStack)}.
     */
    public void applyData() {
        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();

        CompoundTag customTag = rootTag.asCompound()
            .flatMap(c -> c.getCompound("CustomComponents"))
            .orElse(new CompoundTag());

        for (Map.Entry<DataComponentType<?>, DataComponent<?>> entry : components.entrySet()) {
            if (entry.getValue() instanceof Vanilla v && item != null) {
                v.apply(item.getStack());
            } else {
                String id = Registries.DATA_COMPONENT_TYPES.getId(entry.getKey());
                if (id != null) {
                    Tag nbt = encodeComponent(entry.getValue(), NbtOps.INSTANCE);
                    customTag.put(id, nbt);
                }
            }
        }

        rootTag.put("CustomComponents", customTag);

        if (item != null) item.setCTag(root);
        if (entity != null) entity.setCTag(root);
    }

    /**
     * @return A list of all active {@link DataComponent} instances in this map.
     */
    public List<DataComponent<?>> getAllComponents() {
        return new ArrayList<>(components.values());
    }

    /**
     * Internal helper to remove a specific custom component from the NBT tag structure.
     *
     * @param type The type to remove from storage.
     */
    private void removeCustomComponent(DataComponentType<?> type) {
        String id = Registries.DATA_COMPONENT_TYPES.getId(type);
        if (id == null) return;

        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();

        rootTag.asCompound().flatMap(c -> c.getCompound("CustomComponents")).ifPresent(customTag -> {
            if (customTag.contains(id)) {
                customTag.remove(id);
                rootTag.put("CustomComponents", customTag);

                if (item != null) item.setCTag(root);
                if (entity != null) entity.setCTag(root);
            }
        });
    }

    /**
     * Encodes a component into a serialized format using the provided DynamicOps.
     *
     * @param <T>       The component's value type.
     * @param <D>       The target serialization format type (e.g., Tag).
     * @param component The {@link DataComponent} to encode.
     * @param ops       The {@link DynamicOps} logic to use for encoding.
     * @return The encoded data object.
     */
    public static <T, D> D encodeComponent(DataComponent<T> component, DynamicOps<D> ops) {
        return Try.of(() -> {
            @SuppressWarnings("unchecked")
            DataComponentType<DataComponent<T>> type = (DataComponentType<DataComponent<T>>) component.getType();
            return type.codec().encode(ops, component);
        }).get();
    }

    /**
     * Internal helper to parse the {@code CustomComponents} NBT compound and populate the map.
     *
     * @param root The root {@link CTag} containing the custom data.
     */
    private void loadCustomComponents(CTag root) {
        CompoundTag rootTag = root.toVanilla();

        rootTag.asCompound().flatMap(c -> c.getCompound("CustomComponents")).ifPresent(customTag -> {
            for (String id : customTag.keySet()) {
                DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(id);
                if (type == null) continue;

                Tag nbtData = customTag.get(id);
                if (nbtData == null) continue;

                Try.of(() -> type.codec().decode(NbtOps.INSTANCE, nbtData))
                    .onSuccess(decoded -> {
                        if (decoded != null) components.put(type, decoded);
                    })
                    .onFailure(t ->
                        AbyssalLib.getInstance().getLogger().severe("Failed to load component " + id + ": " + t.getMessage())
                    );
            }
        });
    }
}