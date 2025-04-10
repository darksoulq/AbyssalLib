package me.darksoul.abyssalLib.registry;

import me.darksoul.abyssalLib.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DeferredRegistry<T> {
    private final Registry<T> targetRegistry;
    private final String modId;
    private final Map<String, RegistryObject<T>> pending = new LinkedHashMap<>();

    private DeferredRegistry(Registry<T> targetRegistry, String modId) {
        this.targetRegistry = targetRegistry;
        this.modId = modId;
    }

    public static <T> DeferredRegistry<T> create(Registry<T> targetRegistry, String modId) {
        return new DeferredRegistry<>(targetRegistry, modId);
    }

    public RegistryObject<T> register(String name, BiFunction<String, ResourceLocation, T> supplier) {
        if (pending.containsKey(name)) {
            throw new IllegalStateException("Duplicate registration: " + modId + ":" + name);
        }

        ResourceLocation id = new ResourceLocation(modId, name);
        RegistryObject<T> obj = new RegistryObject<>(id.toString(), () -> supplier.apply(name, id));
        pending.put(name, obj);
        return obj;
    }

    public void apply() {
        for (Map.Entry<String, RegistryObject<T>> entry : pending.entrySet()) {
            RegistryObject<T> obj = entry.getValue();
            targetRegistry.register(obj.getId(), (id) -> obj.get());
        }
        pending.clear();
    }
}
