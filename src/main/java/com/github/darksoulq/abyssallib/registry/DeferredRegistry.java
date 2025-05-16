package com.github.darksoulq.abyssallib.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.block.Block;
import com.github.darksoulq.abyssallib.event.custom.RegistryApplyEvent;
import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.registry.object.DeferredObject;
import com.github.darksoulq.abyssallib.util.ResourceLocation;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A deferred registry for registering objects during a plugin's initialization phase,
 * deferring the actual registration until {@link #apply()} is called.
 *
 * <p>This is useful for ensuring that all registrations occur at the correct time
 * in the server lifecycle, and allows mods to define objects ahead of time without
 * immediately applying them to the underlying {@link Registry}.</p>
 *
 * @param <T> the type of object being registered
 */
public class DeferredRegistry<T> extends LinkedHashMap<String, DeferredObject<T>> {
    private final Registry<T> targetRegistry;
    private final String modId;

    /**
     * Internal constructor. Use {@link #create(Registry, String)} instead.
     *
     * @param targetRegistry the target registry this defers to
     * @param modId the mod ID for namespacing registered objects
     */
    private DeferredRegistry(Registry<T> targetRegistry, String modId) {
        this.targetRegistry = targetRegistry;
        this.modId = modId;
    }

    /**
     * Creates a new {@link DeferredRegistry} instance for a specific registry and mod ID.
     *
     * @param targetRegistry the registry to register objects into later
     * @param modId the mod ID to use for namespacing
     * @param <T> the type of object being registered
     * @return a new {@link DeferredRegistry} instance
     */
    public static <T> DeferredRegistry<T> create(Registry<T> targetRegistry, String modId) {
        return new DeferredRegistry<>(targetRegistry, modId);
    }

    /**
     * Registers a new object in this deferred registry.
     *
     * <p>The object will not be available in the target registry until {@link #apply()} is called.
     * Duplicate names are not allowed and will throw an exception.</p>
     *
     * @param name the name of the object (not namespaced)
     * @param supplier a function that creates the object using the name and full namespaced ID
     * @return a {@link DeferredObject} representing the registered entry
     * @throws IllegalStateException if an object with the same name is already registered
     */
    public DeferredObject<T> register(String name, BiFunction<String, ResourceLocation, T> supplier) {
        if (super.containsKey(name)) {
            throw new IllegalStateException("Duplicate registration: " + modId + ":" + name);
        }

        ResourceLocation id = new ResourceLocation(modId, name);
        DeferredObject<T> obj = new DeferredObject<>(id.toString(), () -> supplier.apply(name, id));
        super.put(name, obj);
        return obj;
    }

    /**
     * Applies all registered objects to the target registry.
     *
     * <p>This fires a {@link RegistryApplyEvent}, allowing event listeners to cancel
     * the registration. If cancelled, no objects are registered and a message is logged.</p>
     */
    public void apply() {
        RegistryApplyEvent<T> event = new RegistryApplyEvent<>(targetRegistry, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            AbyssalLib.getInstance().getLogger().info("Registry was cancelled for: " + modId);
            return;
        }
        for (Map.Entry<String, DeferredObject<T>> entry : super.entrySet()) {
            DeferredObject<T> obj = entry.getValue();
            targetRegistry.register(obj.getId(), id -> obj.get());
            if (obj.get() instanceof Block block && block.generateItem()) {
                DeferredObject<Item> blockItem = new DeferredObject<>(obj.getId(), () -> block.item().get());
                BuiltinRegistries.ITEMS.register(obj.getId(), (itemId) -> blockItem.get());
                BuiltinRegistries.BLOCK_ITEMS.register(obj.getId(), (itemName) -> block);
            }
        }
        super.clear();
    }
}
