package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.StringOps;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.datacomponent.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@ApiStatus.Internal
public class ComponentMap {
    private final Map<Identifier, DataComponent<?>> components = new HashMap<>();
    private final Map<Identifier, Vanilla> vanillaComponents = new HashMap<>();
    private final Item item;

    public ComponentMap(Item item) {
        this.item = item;
        load();
    }

    public void load() {
        if (item == null || item.getStack() == null) return;

        for (DataComponentType type : item.getStack().getDataTypes()) {
            Class<? extends DataComponent<?>> cls = Registries.DATA_COMPONENTS.get(type.key().toString());
            if (cls == null) continue;

            try {
                if (type instanceof DataComponentType.Valued<?> vl) {
                    Object val = item.getStack().getData(vl);
                    if (val == null) continue;
                    Constructor<?> cons = Arrays.stream(cls.getConstructors())
                            .filter(c -> c.getParameterCount() == 1 &&
                                    isAssignable(c.getParameterTypes()[0], val.getClass()))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchMethodException("No suitable constructor for value type: " + val.getClass()));
                    vanillaComponents.put(Identifier.of(type.key().toString()), (Vanilla) cons.newInstance(val));
                } else {
                    Constructor<?> cons = cls.getConstructor();
                    vanillaComponents.put(Identifier.of(type.key().toString()), (Vanilla) cons.newInstance());
                }
            } catch (NoSuchMethodException e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to find constructor for vanilla component " + cls.getSimpleName() + ": " + e.getMessage());
            } catch (Exception e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to instantiate vanilla component " + cls.getSimpleName() + ": " + e.getMessage());
            }
        }

        CTag root = item.getCTag();
        CompoundTag tag = root.toVanilla().getCompoundOrEmpty("CustomComponents");
        if (tag.isEmpty()) return;

        for (String cId : tag.keySet()) {
            Class<? extends DataComponent<?>> cls = Registries.DATA_COMPONENTS.get(cId);
            if (cls == null) continue;

            try {
                Optional<String> encoded = tag.getString(cId);
                if (encoded.isEmpty()) continue;

                Field codecField = cls.getDeclaredField("CODEC");
                if (!Modifier.isStatic(codecField.getModifiers())) throw new RuntimeException("Missing static CODEC in " + cls.getName());
                codecField.setAccessible(true);
                Codec<?> codec = (Codec<?>) codecField.get(null);

                DataComponent<?> decoded = (DataComponent<?>) codec.decode(StringOps.INSTANCE, encoded.get());
                if (decoded != null) components.put(decoded.getId(), decoded);

            } catch (NoSuchFieldException e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to find static CODEC field for custom component " + cls.getSimpleName() + ": " + e.getMessage());
            } catch (Codec.CodecException e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to decode custom component " + cId + ": " + e.getMessage());
            } catch (Exception e) {
                AbyssalLib.getInstance().getLogger().severe("Failed to load custom component " + cId + ": " + e.getMessage());
            }
        }
    }

    public void setData(DataComponent<?> component) {
        if (component instanceof Vanilla v) vanillaComponents.put(component.getId(), v);
        else components.put(component.getId(), component);
        applyData();
    }
    public void removeData(Identifier id) {
        if (vanillaComponents.containsKey(id)) {
            vanillaComponents.remove(id);
        }
        else if (components.containsKey(id)) {
            removeComponent(components.get(id));
        }
    }
    public DataComponent<?> getData(Identifier id) {
        if (vanillaComponents.containsKey(id)) return (DataComponent<?>) vanillaComponents.get(id);
        else return components.getOrDefault(id, null);
    }
    @SuppressWarnings("unchecked")
    public <T extends DataComponent<?>> T getData(Class<T> clazz) {
        for (DataComponent<?> cmp : components.values()) {
            if (clazz.isInstance(cmp)) return (T) cmp;
        }
        for (Vanilla v : vanillaComponents.values()) {
            if (clazz.isInstance(v)) return (T) v;
        }
        return null;
    }
    public DataComponent<?> getData(DataComponentType type) {
        Identifier id = getId(type);
        return (DataComponent<?>) vanillaComponents.get(id);
    }
    public void applyData() {
        CTag root = item.getCTag();
        CompoundTag rootTag = root.toVanilla();
        CompoundTag tag = rootTag.getCompoundOrEmpty("CustomComponents");
        CTag data = new CTag(tag);

        for (Map.Entry<Identifier, DataComponent<?>> cmp : components.entrySet()) {
            String encoded = encodeComponent(cmp.getValue(), StringOps.INSTANCE);
            data.set(cmp.getKey().toString(), encoded);
        }
        for (Vanilla v : vanillaComponents.values()) {
            v.apply(item.getStack());
        }
        rootTag.put("CustomComponents", data.toVanilla());
        item.setCTag(root);
    }
    public boolean hasData(Identifier id) {
        return components.containsKey(id) || vanillaComponents.containsKey(id);
    }
    public boolean hasData(DataComponentType type) {
        return vanillaComponents.containsKey(getId(type));
    }
    public <T extends DataComponent<?>> boolean hasData(Class<T> clazz) {
        return getData(clazz) != null;
    }

    public List<DataComponent<?>> getAllComponents() {
        List<DataComponent<?>> toReturn = new ArrayList<>();
        toReturn.addAll(components.values());
        toReturn.addAll(vanillaComponents.values().stream().map(k -> (DataComponent<?>) k).toList());
        return toReturn;
    }
    public List<DataComponent<?>> getVanillaComponents() {
        return new ArrayList<>(vanillaComponents.values().stream().map(k -> (DataComponent<?>) k).toList());
    }
    public List<DataComponent<?>> getCustomComponents() {
        return new ArrayList<>(components.values());
    }
    public List<Identifier> getAllIds() {
        List<Identifier> toReturn = new ArrayList<>();
        toReturn.addAll(components.keySet());
        toReturn.addAll(vanillaComponents.keySet());
        return toReturn;
    }
    public List<Identifier> getVanillaIds() {
        return new ArrayList<>(vanillaComponents.keySet());
    }
    public List<Identifier> getCustomIds() {
        return new ArrayList<>(components.keySet());
    }

    private void removeComponent(DataComponent<?> component) {
        CTag root = item.getCTag();
        CompoundTag rootTag = root.toVanilla();
        CompoundTag tag = rootTag.getCompoundOrEmpty("CustomComponents");
        if (tag.contains(component.getId().toString())) tag.remove(component.getId().toString());
        rootTag.put("CustomComponents", tag);
        item.setCTag(root);
    }
    public static Identifier getId(DataComponentType type) {
        return Identifier.of(type.key().asString());
    }
    private static <T, D> D encodeComponent(DataComponent<T> component, DynamicOps<D> ops) {
        try {
            return component.codec.encode(ops, component);
        } catch (Codec.CodecException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean isAssignable(Class<?> paramType, Class<?> valueType) {
        if (paramType.isPrimitive()) {
            if (paramType == int.class && valueType == Integer.class) return true;
            if (paramType == long.class && valueType == Long.class) return true;
            if (paramType == boolean.class && valueType == Boolean.class) return true;
            if (paramType == double.class && valueType == Double.class) return true;
            if (paramType == float.class && valueType == Float.class) return true;
            if (paramType == char.class && valueType == Character.class) return true;
            if (paramType == byte.class && valueType == Byte.class) return true;
            if (paramType == short.class && valueType == Short.class) return true;
            return false;
        }
        return paramType.isAssignableFrom(valueType);
    }
}
