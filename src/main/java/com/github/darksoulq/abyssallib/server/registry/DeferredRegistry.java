package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.custom.server.RegistryApplyEvent;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.item.Item;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public final class DeferredRegistry<T> {
    private final Registry<T> registry;
    private final String pluginId;
    private final Map<String, Holder<T>> entries = new HashMap<>();

    private DeferredRegistry(@NotNull Registry<T> registry, @NotNull String pluginId) {
        this.registry = registry;
        this.pluginId = pluginId;
    }

    public static <T> DeferredRegistry<T> create(Registry<T> registry, String pluginId) {
        return new DeferredRegistry<>(registry, pluginId);
    }

    public Holder<T> register(String name, Function<Identifier, T> supplier) {
        if (entries.containsKey(name)) {
            throw new IllegalStateException("Duplicate deferred registration: " + pluginId + ":" + name);
        }
        Identifier id = Identifier.of(pluginId, name);
        Holder<T> holder = new Holder<>(() -> supplier.apply(id));
        entries.put(name, holder);
        return holder;
    }

    public void apply() {
        RegistryApplyEvent<T> event = new RegistryApplyEvent<>(registry, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            AbyssalLib.getInstance().getLogger().info("Registry apply cancelled for mod: " + pluginId);
            return;
        }
        List<DamageType> damageTypes = new ArrayList<>();

        for (Map.Entry<String, Holder<T>> entries : entries.entrySet()) {
            T value = entries.getValue().get();
            registry.register(pluginId + ":" + entries.getKey(), value);

            if (value instanceof DamageType) damageTypes.add((DamageType) value);
            if (value instanceof CustomBlock block && block.generateItem()) {
                Item blockItem = block.getItem().get();
                Registries.ITEMS.register(pluginId + ":" + entries.getKey(), blockItem);
            }
        }
        if (!damageTypes.isEmpty()) AbyssalLib.DAMAGE_TYPE_REGISTRAR.register(damageTypes);

        entries.clear();
    }

    public Collection<Holder<T>> getEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public String getPluginId() {
        return pluginId;
    }
}
