package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.custom.server.RegistryApplyEvent;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomMarker;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * A specialized registry utility that defers object registration until the server is ready.
 * <p>
 * This pattern allows developers to define registry entries in static fields before the
 * underlying {@link Registry} is fully initialized. When {@link #apply()} is called,
 * all queued entries are instantiated and moved into the main registry.
 *
 * @param <T> The type of objects handled by this registry.
 */
public final class DeferredRegistry<T> {
    /** The target master registry where objects will eventually reside. */
    private final Registry<T> registry;
    /** The unique namespace/mod ID associated with this deferred registry. */
    private final String pluginId;
    /** A local map of pending entries waiting to be applied. */
    private final Map<String, Holder<T>> entries = new HashMap<>();

    /**
     * Private constructor for the factory method.
     *
     * @param registry The target {@link Registry}.
     * @param pluginId The namespace for these entries.
     */
    private DeferredRegistry(@NotNull Registry<T> registry, @NotNull String pluginId) {
        this.registry = registry;
        this.pluginId = pluginId;
    }

    /**
     * Factory method to create a new deferred registry instance.
     *
     * @param <T>      The registry type.
     * @param registry The base {@link Registry}.
     * @param pluginId The namespace for the objects.
     * @return A new {@link DeferredRegistry}.
     */
    public static <T> DeferredRegistry<T> create(Registry<T> registry, String pluginId) {
        return new DeferredRegistry<>(registry, pluginId);
    }

    /**
     * Queues an object for registration.
     * <p>
     * Instead of returning the object directly, this returns a {@link Holder}, which
     * allows for lazy initialization.
     *
     * @param name     The unique name (path) within this registry's namespace.
     * @param supplier A function that creates the object using the generated {@link Identifier}.
     * @return A {@link Holder} wrapping the future object.
     * @throws IllegalStateException if the name has already been registered in this deferred registry.
     */
    public Holder<T> register(String name, Function<Identifier, T> supplier) {
        if (entries.containsKey(name)) {
            throw new IllegalStateException("Duplicate deferred registration: " + pluginId + ":" + name);
        }
        Identifier id = Identifier.of(pluginId, name);
        Holder<T> holder = new Holder<>(() -> supplier.apply(id));
        entries.put(name, holder);
        return holder;
    }

    /**
     * Finalizes the registration process by moving all deferred entries into the master registry.
     * <p>
     * This method performs several side effects:
     * <ul>
     * <li>Fires a {@link RegistryApplyEvent} to allow other plugins to modify or cancel the process.</li>
     * <li>Registers {@link DamageType}s with the global damage registrar.</li>
     * <li>Automatically generates Item entries and Predicates for {@link CustomBlock}s.</li>
     * <li>Clears the internal staging map once complete.</li>
     * </ul>
     */
    public void apply() {
        RegistryApplyEvent<T> event = new RegistryApplyEvent<>(registry, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            AbyssalLib.getInstance().getLogger().info("Registry apply cancelled for plugin: " + pluginId);
            return;
        }
        List<DamageType> damageTypes = new ArrayList<>();

        for (Map.Entry<String, Holder<T>> entry : entries.entrySet()) {
            T value = entry.getValue().get();
            String id = pluginId + ":" + entry.getKey();
            registry.register(id, value);

            if (value instanceof DamageType) damageTypes.add((DamageType) value);
            if (value instanceof CustomBlock block && block.generateItem()) {
                Item blockItem = block.getItem().get();
                Registries.PREDICATES.register(id, ItemPredicate.builder()
                    .value(new CustomMarker(Identifier.of(id)))
                    .build());
                Registries.ITEMS.register(id, blockItem);
            }
            if (value instanceof Item) {
                Registries.PREDICATES.register(id, ItemPredicate.builder()
                    .value(new CustomMarker(Identifier.of(id)))
                    .build());
            }
        }
        if (!damageTypes.isEmpty()) AbyssalLib.DAMAGE_TYPE_REGISTRAR.register(damageTypes);

        entries.clear();
    }

    /** @return An unmodifiable collection of all pending {@link Holder} entries. */
    public Collection<Holder<T>> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    /** @return The namespace associated with this registry. */
    public String getPluginId() {
        return pluginId;
    }
}