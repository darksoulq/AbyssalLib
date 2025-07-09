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
import java.util.function.Function;

/**
 * A registry that defers registration of objects until explicitly applied.
 *
 * @param <T> The type of objects being registered.
 */
public final class DeferredRegistry<T> {

    /**
     * The target registry to apply registered entries to.
     */
    private final Registry<T> targetRegistry;

    /**
     * The mod ID used for namespacing.
     */
    private final String modId;

    /**
     * The map of name to deferred registration objects.
     */
    private final Map<String, DeferredObject<T>> deferredObjects = new LinkedHashMap<>();

    /**
     * Constructs a new deferred registry.
     *
     * @param targetRegistry The registry to apply registrations to.
     * @param modId          The mod ID used for namespacing.
     */
    private DeferredRegistry(Registry<T> targetRegistry, String modId) {
        this.targetRegistry = Objects.requireNonNull(targetRegistry, "targetRegistry");
        this.modId = Objects.requireNonNull(modId, "modId");
    }

    /**
     * Creates a new deferred registry for a specific target and mod ID.
     *
     * @param targetRegistry The registry to apply registrations to.
     * @param modId          The mod ID used for namespacing.
     * @param <T>            The type of objects being registered.
     * @return A new DeferredRegistry instance.
     */
    public static <T> DeferredRegistry<T> create(Registry<T> targetRegistry, String modId) {
        return new DeferredRegistry<>(targetRegistry, modId);
    }

    /**
     * Registers a new object by name using the provided supplier.
     *
     * @param name     The name of the object (unqualified).
     * @param supplier A function that takes an {@link Identifier} and returns an object to register.
     * @return The deferred object wrapper.
     * @throws IllegalStateException If the name is already registered.
     */
    public DeferredObject<T> register(String name, Function<Identifier, T> supplier) {
        if (deferredObjects.containsKey(name)) {
            throw new IllegalStateException("Duplicate deferred registration: " + modId + ":" + name);
        }
        Identifier id = Identifier.of(modId, name);
        DeferredObject<T> deferredObject = new DeferredObject<>(id.toString(), () -> supplier.apply(id));
        deferredObjects.put(name, deferredObject);
        return deferredObject;
    }

    /**
     * Applies all deferred registrations to the target registry.
     * <p>
     * Triggers a {@link RegistryApplyEvent}, which may be cancelled to prevent registration.
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
                Item blockItem = block.getItem().get();
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
     * Returns an unmodifiable collection of all deferred objects.
     *
     * @return A collection of {@link DeferredObject}.
     */
    public Collection<DeferredObject<T>> getDeferredObjects() {
        return Collections.unmodifiableCollection(deferredObjects.values());
    }

    /**
     * Returns the mod ID associated with this registry.
     *
     * @return The mod ID string.
     */
    public String getModId() {
        return modId;
    }
}
