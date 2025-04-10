package me.darksoul.abyssalLib.mod;

import me.darksoul.abyssalLib.registry.Registry;

public class ModRegistry extends Registry<ModContainer> {
    public void registerMod(Class<?> clazz) {
        AbyssalMod annotation = clazz.getAnnotation(AbyssalMod.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @AbyssalMod");
        }

        String modId = annotation.name();
        if (contains(modId)) {
            throw new IllegalStateException("Mod with ID '" + modId + "' already registered");
        }

        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            register(modId, (id) -> new ModContainer(modId, instance, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate mod class " + clazz.getName(), e);
        }
    }
}
