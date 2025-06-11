package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.custom.server.RegistryApplyEvent;
import com.github.darksoulq.abyssallib.server.registry.object.DeferredObject;
import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.entity.DamageType;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Defers registrations until apply() is called.
 * Allows batch registration with mod namespacing.
 */
public final class DeferredRegistry<T> {

    private final Registry<T> targetRegistry;
    private final String modId;
    private final Map<String, DeferredObject<T>> deferredObjects = new LinkedHashMap<>();

    private DeferredRegistry(Registry<T> targetRegistry, String modId) {
        this.targetRegistry = Objects.requireNonNull(targetRegistry, "targetRegistry");
        this.modId = Objects.requireNonNull(modId, "modId");
    }

    public static <T> DeferredRegistry<T> create(Registry<T> targetRegistry, String modId) {
        return new DeferredRegistry<>(targetRegistry, modId);
    }

    /**
     * Register a deferred object with simple name and supplier.
     * @throws IllegalStateException if duplicate.
     */
    public DeferredObject<T> register(String name, BiFunction<String, Identifier, T> supplier) {
        if (deferredObjects.containsKey(name)) {
            throw new IllegalStateException("Duplicate deferred registration: " + modId + ":" + name);
        }
        Identifier id = Identifier.of(modId, name);
        DeferredObject<T> deferredObject = new DeferredObject<>(id.toString(), () -> supplier.apply(name, id));
        deferredObjects.put(name, deferredObject);
        return deferredObject;
    }

    /**
     * Apply all deferred registrations to the target registry.
     * Fires RegistryApplyEvent that can cancel the whole batch.
     */
    public void apply() {
        RegistryApplyEvent<T> event = new RegistryApplyEvent<>(targetRegistry, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            AbyssalLib.getInstance().getLogger().info("Registry apply cancelled for mod: " + modId);
            return;
        }

        List<DeferredObject<T>> objects = deferredObjects.values().stream().toList();
        List<DamageType> damageTypes = new ArrayList<>();

        for (DeferredObject<T> obj : objects) {
            T value = obj.get();

            targetRegistry.register(obj.getId(), value);

            if (value instanceof DamageType) {
                damageTypes.add((DamageType) value);
            }

            if (value instanceof Block block && block.generateItem()) {
                Item blockItem = block.item().get();
                BuiltinRegistries.ITEMS.register(obj.getId(), blockItem);
                BuiltinRegistries.BLOCK_ITEMS.register(obj.getId(), block);
            }
        }
        if (!damageTypes.isEmpty()) {
            AbyssalLib.DAMAGE_TYPE_REGISTRAR.register(damageTypes);
        }
        deferredObjects.clear();
    }

    /**
     * Returns all deferred objects registered so far.
     */
    public Collection<DeferredObject<T>> getDeferredObjects() {
        return Collections.unmodifiableCollection(deferredObjects.values());
    }

    public String getModId() {
        return modId;
    }
}
