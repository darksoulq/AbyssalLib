package me.darksoul.abyssalLib.mod;

public class ModContainer {
    private final String modId;
    private final Object instance;
    private final Class<?> modClass;

    public ModContainer(String modId, Object instance, Class<?> modClass) {
        this.modId = modId;
        this.instance = instance;
        this.modClass = modClass;
    }

    public String getModId() {
        return modId;
    }

    public Object getInstance() {
        return instance;
    }

    public Class<?> getModClass() {
        return modClass;
    }
}
