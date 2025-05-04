package me.darksoul.abyssalLib.registry;

import me.darksoul.abyssalLib.AbyssalLib;
import me.darksoul.abyssalLib.event.custom.RegistryApplyEvent;
import me.darksoul.abyssalLib.registry.object.DeferredObject;
import me.darksoul.abyssalLib.util.ResourceLocation;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DeferredRegistry<T> extends LinkedHashMap<String, DeferredObject<T>> {
    private final Registry<T> targetRegistry;
    private final String modId;

    private DeferredRegistry(Registry<T> targetRegistry, String modId) {
        this.targetRegistry = targetRegistry;
        this.modId = modId;
    }

    public static <T> DeferredRegistry<T> create(Registry<T> targetRegistry, String modId) {
        return new DeferredRegistry<>(targetRegistry, modId);
    }

    public DeferredObject<T> register(String name, BiFunction<String, ResourceLocation, T> supplier) {
        if (super.containsKey(name)) {
            throw new IllegalStateException("Duplicate registration: " + modId + ":" + name);
        }

        ResourceLocation id = new ResourceLocation(modId, name);
        DeferredObject<T> obj = new DeferredObject<>(id.toString(), () -> supplier.apply(name, id));
        super.put(name, obj);
        return obj;
    }

    public void apply() {
        RegistryApplyEvent<T> event = new RegistryApplyEvent<>(targetRegistry, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            AbyssalLib.getInstance().getLogger().info("Registry was cancelled for: " + modId);
        }
        for (Map.Entry<String, DeferredObject<T>> entry : super.entrySet()) {
            DeferredObject<T> obj = entry.getValue();
            targetRegistry.register(obj.getId(), id -> obj.get());
        }
        super.clear();
    }
}
