package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.custom.server.RegistryApplyEvent;
import com.github.darksoulq.abyssallib.server.event.internal.ServerEvents;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.advancement.Advancement;
import com.github.darksoulq.abyssallib.world.block.BlockPredicate;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.EntityPredicate;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * A specialized registry utility that defers object registration until the server is ready.
 * This pattern allows developers to define registry entries in static fields before the
 * underlying {@link Registry} is fully initialized. When {@link #apply()} is called,
 * all queued entries are instantiated and moved into the main registry.
 *
 * @param <T>
 * The type of objects handled by this registry.
 */
public final class DeferredRegistry<T> {

    /**
     * The target master registry where objects will eventually reside.
     */
    private final Registry<T> registry;

    /**
     * The unique namespace or plugin ID associated with this deferred registry.
     */
    private final String pluginId;

    /**
     * A local map of pending entries waiting to be applied, keyed by their path.
     */
    private final Map<String, Holder<T>> entries = new HashMap<>();

    /**
     * Private constructor for the factory method.
     *
     * @param registry
     * The target {@link Registry} instance.
     * @param pluginId
     * The namespace for these entries.
     */
    private DeferredRegistry(@NotNull Registry<T> registry, @NotNull String pluginId) {
        this.registry = registry;
        this.pluginId = pluginId;
    }

    /**
     * Factory method to create a new deferred registry instance.
     *
     * @param <T>
     * The registry type.
     * @param registry
     * The base {@link Registry} instance.
     * @param pluginId
     * The namespace for the objects.
     * @return
     * A new {@link DeferredRegistry} instance.
     */
    public static <T> DeferredRegistry<T> create(Registry<T> registry, String pluginId) {
        return new DeferredRegistry<>(registry, pluginId);
    }

    /**
     * Queues an object for registration.
     * Instead of returning the object directly, this returns a {@link Holder}, which
     * allows for lazy initialization and safe cross-referencing during static init.
     *
     * @param name
     * The unique name (path) within this registry's namespace.
     * @param supplier
     * A function that creates the object using the generated {@link Key}.
     * @return
     * A {@link T}  registered object.
     * @throws IllegalStateException
     * If the name has already been registered in this deferred registry.
     */
    public T register(String name, Function<Key, T> supplier) {
        if (entries.containsKey(name)) {
            throw new IllegalStateException("Duplicate deferred registration: " + pluginId + ":" + name);
        }
        Key id = Key.key(pluginId, name);
        Holder<T> holder = new Holder<>(() -> supplier.apply(id));
        entries.put(name, holder);
        return holder.get();
    }

    /**
     * Finalizes the registration process by moving all deferred entries into the master registry.
     * This method triggers the instantiation of all {@link Holder} objects and handles
     * secondary registration for blocks and items.
     */
    public void apply() {
        RegistryApplyEvent<T> event = new RegistryApplyEvent<>(registry, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            AbyssalLib.getInstance().getLogger().info("Registry apply cancelled for plugin: " + pluginId);
            return;
        }

        List<AdvancementHolder> newAdvancements = new ArrayList<>();
        Map<AdvancementHolder, Advancement> customAdvancementMap = new HashMap<>();

        for (Map.Entry<String, Holder<T>> entry : entries.entrySet()) {
            T value = entry.getValue().get();
            String idString = pluginId + ":" + entry.getKey();
            registry.register(idString, value);

            if (value instanceof CustomBlock block && block.generateItem()) {
                Registries.BLOCK_PREDICATES.register(idString, BlockPredicate.builder()
                    .id(Key.key(idString))
                    .build());
                Item blockItem = block.getItem().get();
                Registries.ITEM_PREDICATES.register(idString, ItemPredicate.builder()
                    .id(Key.key(idString))
                    .build());
                Registries.ITEMS.register(idString, blockItem);
            }

            if (value instanceof Item) {
                Registries.ITEM_PREDICATES.register(idString, ItemPredicate.builder()
                    .id(Key.key(idString))
                    .build());
            }

            if (value instanceof CustomEntity<?>) {
                Registries.ENTITY_PREDICATES.register(idString, EntityPredicate.builder()
                    .id(Key.key(idString))
                    .build());
            }

            if (value instanceof Advancement customAdv) {
                AdvancementHolder holder = customAdv.toNMSHolder();
                newAdvancements.add(holder);
                customAdvancementMap.put(holder, customAdv);

                ServerAdvancementManager manager = MinecraftServer.getServer().getAdvancements();
                Map<Identifier, AdvancementHolder> mutableAdvancements = new HashMap<>(manager.advancements);
                mutableAdvancements.put(holder.id(), holder);
                manager.advancements = mutableAdvancements;
            }
        }

        ServerEvents.applyAdvancementLayout(newAdvancements, customAdvancementMap);

        entries.clear();
    }

    /**
     * Retrieves an unmodifiable collection of all pending holders in this registry.
     *
     * @return
     * A collection of {@link Holder} entries.
     */
    public Collection<Holder<T>> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    /**
     * Retrieves the namespace associated with this deferred registry.
     *
     * @return
     * The plugin or mod namespace string.
     */
    public String getPluginId() {
        return pluginId;
    }
}