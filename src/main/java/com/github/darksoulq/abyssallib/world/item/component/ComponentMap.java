package com.github.darksoulq.abyssallib.world.item.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.item.Item;
import io.papermc.paper.datacomponent.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class ComponentMap {
    public static final Map<Class<?>, Codec<?>> COMPONENT_CODEC_CACHE = new ConcurrentHashMap<>();
    private final Map<Identifier, DataComponent<?>> components = new HashMap<>();
    private final Map<Identifier, Vanilla> vanillaComponents = new HashMap<>();
    private final Item item;
    private final Entity<? extends LivingEntity> entity;

    public ComponentMap(Item item) {
        this.item = item;
        this.entity = null;
        load();
    }

    public ComponentMap(Entity<? extends LivingEntity> entity) {
        this.item = null;
        this.entity = null;
        load();
    }

    public void load() {
        if (this.item != null) loadItem();
        if (this.entity != null) loadEntity();
    }

    public void loadItem() {
        if (item == null || item.getStack() == null) return;

        for (DataComponentType type : item.getStack().getDataTypes()) {
            Class<? extends DataComponent<?>> cls = Registries.DATA_COMPONENTS.get(type.key().toString());
            if (cls == null) continue;

            if (type instanceof DataComponentType.Valued<?> vl && item.getStack().getData(vl) == null) {
                continue;
            }

            Try.of(() -> {
                if (type instanceof DataComponentType.Valued<?> vl) {
                    Object val = item.getStack().getData(vl);
                    Constructor<?> cons = Arrays.stream(cls.getConstructors())
                        .filter(c -> c.getParameterCount() == 1 &&
                            isAssignable(c.getParameterTypes()[0], val.getClass()))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchMethodException("No suitable constructor for value type: " + val.getClass()));
                    return (Vanilla) cons.newInstance(val);
                } else {
                    Constructor<?> cons = cls.getConstructor();
                    return (Vanilla) cons.newInstance();
                }
            }).onSuccess(vanillaComponent ->
                vanillaComponents.put(Identifier.of(type.key().toString()), vanillaComponent)
            ).onFailure(t ->
                AbyssalLib.getInstance().getLogger().severe("Failed to load vanilla component " + cls.getSimpleName() + ": " + t.getMessage())
            );
        }

        loadCustomComponents(item.getCTag());
    }

    public void loadEntity() {
        loadCustomComponents(entity.getCTag());
    }

    public void setData(DataComponent<?> component) {
        if (hasData(component.getId())) removeData(component.getId());
        if (component instanceof Vanilla v) vanillaComponents.put(component.getId(), v);
        else components.put(component.getId(), component);
        applyData();
    }

    public void removeData(Identifier id) {
        if (vanillaComponents.containsKey(id)) {
            Vanilla v = vanillaComponents.remove(id);
            if (item != null) v.remove(item.getStack());
        } else if (components.containsKey(id)) {
            removeComponent(components.get(id));
        }
    }

    public void removeData(Class<? extends DataComponent> clazz) {
        for (DataComponent<?> cmp : components.values()) {
            if (clazz.isInstance(cmp)) components.remove(cmp.getId());
        }
        for (Vanilla v : vanillaComponents.values()) {
            if (clazz.isInstance(v)) {
                vanillaComponents.remove(((DataComponent<?>) v).getId());
                if (item != null) v.remove(item.getStack());
            }
        }
    }

    public DataComponent<?> getData(Identifier id) {
        if (vanillaComponents.containsKey(id)) return (DataComponent<?>) vanillaComponents.get(id);
        else return components.getOrDefault(id, null);
    }

    @SuppressWarnings("unchecked")
    public <C extends DataComponent<?>> C getData(Class<C> clazz) {
        for (DataComponent<?> cmp : components.values()) {
            if (clazz.isInstance(cmp)) return (C) cmp;
        }
        for (Vanilla v : vanillaComponents.values()) {
            if (clazz.isInstance(v)) return (C) v;
        }
        return null;
    }

    public DataComponent<?> getData(DataComponentType type) {
        Identifier id = getId(type);
        return (DataComponent<?>) vanillaComponents.get(id);
    }

    public void applyData() {
        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();
        CompoundTag tag = rootTag.getCompoundOrEmpty("CustomComponents");
        CTag data = new CTag(tag);
        data.clear();

        for (Map.Entry<Identifier, DataComponent<?>> cmp : components.entrySet()) {
            JsonNode json = encodeComponent(cmp.getValue(), JsonOps.INSTANCE);
            String encoded = json.toString();
            data.set(cmp.getKey().toString(), encoded);
        }
        if (item != null) {
            for (Vanilla v : vanillaComponents.values()) {
                v.apply(item.getStack());
            }
        }
        rootTag.put("CustomComponents", data.toVanilla());
        if (item != null) item.setCTag(root);
        if (entity != null) entity.setCTag(root);
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
        CTag root = item != null ? item.getCTag() : entity.getCTag();
        CompoundTag rootTag = root.toVanilla();
        CompoundTag tag = rootTag.getCompoundOrEmpty("CustomComponents");
        if (tag.contains(component.getId().toString())) tag.remove(component.getId().toString());
        rootTag.put("CustomComponents", tag);
        if (item != null) item.setCTag(root);
        if (entity != null) entity.setCTag(root);
    }

    public static Identifier getId(DataComponentType type) {
        return Identifier.of(type.key().asString());
    }

    public static <T, D> D encodeComponent(DataComponent<T> component, DynamicOps<D> ops) {
        return Try.of(() -> component.codec.encode(ops, component)).get();
    }

    private void loadCustomComponents(CTag root) {
        CompoundTag tag = root.toVanilla().getCompoundOrEmpty("CustomComponents");
        if (tag.isEmpty()) return;

        for (String cId : tag.keySet()) {
            Class<? extends DataComponent<?>> cls = Registries.DATA_COMPONENTS.get(cId);
            if (cls == null) continue;

            Optional<String> encoded = tag.getString(cId);
            if (encoded.isEmpty()) continue;

            Try.of(() -> {
                JsonNode json = new ObjectMapper().readTree(encoded.get());

                Codec<?> codec = COMPONENT_CODEC_CACHE.computeIfAbsent(cls, k ->
                    Try.of(() -> {
                        Field codecField = k.getDeclaredField("CODEC");
                        if (!Modifier.isStatic(codecField.getModifiers())) {
                            throw new NoSuchFieldException("Field CODEC must be static");
                        }
                        codecField.setAccessible(true);
                        return (Codec<?>) codecField.get(null);
                    }).orElseThrow(e -> new RuntimeException("Failed to retrieve Codec for " + k.getSimpleName(), e))
                );
                return (DataComponent<?>) codec.decode(JsonOps.INSTANCE, json);
            }).onSuccess(decoded -> {
                if (decoded != null) components.put(decoded.getId(), decoded);
            }).onFailure(t -> {
                String context;
                if (t instanceof NoSuchFieldException) {
                    context = "Failed to find static CODEC field for custom component " + cls.getSimpleName();
                } else if (t instanceof Codec.CodecException) {
                    context = "Failed to decode custom component " + cId;
                } else {
                    context = "Failed to load custom component " + cId;
                }
                AbyssalLib.getInstance().getLogger().severe(context + ": " + t.getMessage());
            });
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