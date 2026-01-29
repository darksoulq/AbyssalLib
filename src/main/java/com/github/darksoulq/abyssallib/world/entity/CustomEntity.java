package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.PDCTag;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.entity.internal.NMSGoalHandler;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CustomEntity<T extends LivingEntity> implements Cloneable {
    public UUID uuid = null;
    private Identifier id;
    private Class<T> baseClass;
    private SpawnCategory category;
    private SpawnSettings spawnSettings;

    private final Map<Integer, Function<T, Goal>> pathfinderGoals = new LinkedHashMap<>();
    private final Map<Integer, Function<T, Goal>> targetGoals = new LinkedHashMap<>();
    private final Map<Attribute, Double> attributes = new LinkedHashMap<>();
    private ComponentMap componentMap;
    private final Map<Attribute, List<AttributeModifier>> modifiers = new LinkedHashMap<>();

    public CustomEntity(Identifier id, Class<T> baseClass, SpawnCategory category) {
        this.id = id;
        this.baseClass = baseClass;
        this.category = category;
    }

    public void addGoal(int priority, Function<T, Goal> goal) {
        pathfinderGoals.put(priority, goal);
    }
    public void addTargetGoal(int priority, Function<T, Goal> goal) {
        targetGoals.put(priority, goal);
    }

    public void setAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }
    public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        modifiers.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    }

    public void setSpawnSettings(SpawnSettings settings) {
        this.spawnSettings = settings;
    }
    public @Nullable SpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    public void spawn(Location loc) {
        spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }
    public void spawn(Location loc, CustomEntitySpawnEvent.SpawnReason reason) {
        if (uuid != null) return;
        T entity = loc.getWorld().spawn(loc, baseClass);
        this.uuid = entity.getUniqueId();

        CustomEntitySpawnEvent event = EventBus.post(new CustomEntitySpawnEvent(this, reason));
        if (event.isCancelled()) {
            entity.remove();
            return;
        }

        onSpawn();
        applyGoals();
        applyAttributes();
        applyComponents();

        EntityManager.add(this);
    }
    public void spawn(T entity) {
        spawn(entity, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }
    public void spawn(T entity, CustomEntitySpawnEvent.SpawnReason reason) {
        if (uuid != null) return;
        this.uuid = entity.getUniqueId();

        CustomEntitySpawnEvent event = EventBus.post(new CustomEntitySpawnEvent(this, reason));
        if (event.isCancelled()) return;

        onSpawn();
        applyGoals();
        applyAttributes();
        applyComponents();
        EntityManager.add(this);
    }

    public void setData(DataComponent<?> component) {
        componentMap.setData(component);
    }
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        return componentMap.getData(type);
    }
    public boolean hasData(DataComponentType<?> type) {
        return componentMap.hasData(type);
    }
    public void unsetData(DataComponentType<?> type) {
        componentMap.removeData(type);
    }

    public void applyGoals() {
        T entity = getBaseEntity().orElseThrow(() -> new IllegalStateException("Base entity is null"));
        NMSGoalHandler.clearGoals(entity);

        pathfinderGoals.forEach((priority, goal) ->
                NMSGoalHandler.addGoal(entity, goal.apply(entity), priority)
        );

        targetGoals.forEach((priority, goal) ->
                NMSGoalHandler.addTargetGoal(entity, goal.apply(entity), priority)
        );
    }
    public void applyAttributes() {
        T entity = getBaseEntity().orElseThrow(() -> new IllegalStateException("Base entity is null"));
        for (Map.Entry<Attribute, Double> entry : attributes.entrySet()) {
            AttributeInstance instance = entity.getAttribute(entry.getKey());
            if (instance != null) {
                instance.setBaseValue(entry.getValue());
            }
        }

        for (Map.Entry<Attribute, List<AttributeModifier>> entry : modifiers.entrySet()) {
            AttributeInstance instance = entity.getAttribute(entry.getKey());
            if (instance != null) {
                for (AttributeModifier modifier : entry.getValue()) {
                    instance.addModifier(modifier);
                }
            }
        }
    }
    public void applyComponents() {
        componentMap = new ComponentMap(this);
    }

    public Identifier getId() {
        return id;
    }
    public Optional<PDCTag> getData() {
        if (uuid == null) return Optional.empty();

        LivingEntity entity = (LivingEntity) org.bukkit.Bukkit.getEntity(uuid);
        if (entity == null) return Optional.empty();

        PersistentDataContainer container = entity.getPersistentDataContainer();
        return Optional.of(new PDCTag(container));
    }
    public CTag getCTag() {
        if (uuid == null) return null;

        if (getBaseEntity().isEmpty()) return null;
        LivingEntity entity = getBaseEntity().get();

        CustomData dta = ((CraftLivingEntity) entity).getHandle().get(DataComponents.CUSTOM_DATA);
        if (dta == null) dta = CustomData.EMPTY;

        CompoundTag tag = dta.copyTag();
        if (tag.getCompound("CustomData").isPresent()) {
            CompoundTag custom = tag.getCompound("CustomData").get();
            return new CTag(custom);
        } else {
            tag.put("CustomData", new CompoundTag());
            return new CTag(tag.getCompound("CustomData").get());
        }
    }
    public void setCTag(CTag container) {
        if (uuid == null) return;

        if (getBaseEntity().isEmpty()) return;
        LivingEntity entity = getBaseEntity().get();

        CustomData data = ((CraftLivingEntity) entity).getHandle().get(DataComponents.CUSTOM_DATA);
        if (data == null) data = CustomData.EMPTY;
        CompoundTag tag = data.copyTag();
        tag.put("CustomData", container.toVanilla());
        data = CustomData.of(tag);
        ((CraftLivingEntity) entity).getHandle().setComponent(DataComponents.CUSTOM_DATA, data);
    }
    @SuppressWarnings("unchecked")
    public Optional<T> getBaseEntity() {
        return Optional.ofNullable((T) Bukkit.getEntity(uuid));
    }
    public SpawnCategory getCategory() {
        return category;
    }

    public void onSpawn() {}
    public ActionResult onDeath(EntityDeathEvent event) { return ActionResult.PASS; }
    public void onUnload() {}
    public void onLoad() {}

    public static CustomEntity<? extends LivingEntity> resolve(org.bukkit.entity.Entity entity) {
        return EntityManager.get(entity.getUniqueId());
    }
    @SuppressWarnings("unchecked")
    @Override
    public CustomEntity<T> clone() {
        try {
            CustomEntity<T> copy = (CustomEntity<T>) super.clone();

            copy.id = id;
            copy.baseClass = baseClass;
            copy.category = category;
            copy.spawnSettings = spawnSettings;

            this.pathfinderGoals.forEach(copy::addGoal);
            this.targetGoals.forEach(copy::addTargetGoal);
            this.attributes.forEach(copy::setAttribute);

            this.modifiers.forEach((attr, list) ->
                    list.forEach(mod -> copy.addAttributeModifier(attr, mod))
            );
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class SpawnSettings {
        public int weight;
        public int minPack;
        public int maxPack;
        public int minY = ServerLevel.MIN_ENTITY_SPAWN_Y;
        public int maxY = ServerLevel.MAX_ENTITY_SPAWN_Y;
        public int minLight = 0;
        public int maxLight = 15;
        public boolean requireSkyDarkness;
        public boolean requireSkyAccess;
        public SpawnPlacement placement;
        public HeightMap heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES;
        public Set<NamespacedKey> biomes = new HashSet<>();
        public BiPredicate<World, Location> canSpawn;

        private SpawnSettings(int weight, int minPack, int maxPack, SpawnPlacement placement) {
            this.weight = Math.max(0, weight);
            this.minPack = Math.max(1, minPack);
            this.maxPack = Math.max(this.minPack, maxPack);
            this.placement = placement;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int weight = 1;
            private int minPack = 1;
            private int maxPack = 1;
            private int minY = ServerLevel.MIN_ENTITY_SPAWN_Y;
            private int maxY = ServerLevel.MAX_ENTITY_SPAWN_Y;
            private int minLight = 0;
            private int maxLight = 15;
            private boolean requireSkyDarkness;
            private boolean requireSkyAccess;
            private SpawnPlacement placement = SpawnPlacement.ON_GROUND;
            private HeightMap heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES;
            private final Set<NamespacedKey> biomes = new HashSet<>();
            private BiPredicate<World, Location> canSpawn;

            public Builder weight(int weight) { this.weight = weight; return this; }
            public Builder pack(int min, int max) { this.minPack = min; this.maxPack = max; return this; }
            public Builder placement(SpawnPlacement placement) { this.placement = placement; return this; }
            public Builder heightRange(int minY, int maxY) { this.minY = minY; this.maxY = maxY; return this; }
            public Builder heightMap(HeightMap map) { this.heightMap = map; return this; }
            public Builder light(int min, int max) { this.minLight = min; this.maxLight = max; return this; }
            public Builder nightOnly() { this.requireSkyDarkness = true; this.maxLight = Math.min(this.maxLight, 7); return this; }
            public Builder requireSkyAccess() { this.requireSkyAccess = true; return this; }
            public Builder biome(NamespacedKey biome) { this.biomes.add(biome); return this; }
            public Builder biomes(Collection<NamespacedKey> biomes) { this.biomes.addAll(biomes); return this; }
            public Builder canSpawn(BiPredicate<World, Location> predicate) { this.canSpawn = predicate; return this; }
            public Builder groundMob() { this.placement = SpawnPlacement.ON_GROUND; this.heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES; return this; }
            public Builder waterMob() { this.placement = SpawnPlacement.IN_WATER; this.heightMap = HeightMap.OCEAN_FLOOR; return this; }
            public Builder lavaMob() { this.placement = SpawnPlacement.IN_LAVA; this.heightMap = HeightMap.MOTION_BLOCKING; return this; }

            public SpawnSettings build() {
                SpawnSettings s = new SpawnSettings(weight, minPack, maxPack, placement);
                s.minY = minY;
                s.maxY = maxY;
                s.minLight = minLight;
                s.maxLight = maxLight;
                s.requireSkyDarkness = requireSkyDarkness;
                s.requireSkyAccess = requireSkyAccess;
                s.heightMap = heightMap;
                s.biomes.addAll(biomes);
                s.canSpawn = canSpawn;
                return s;
            }
        }
    }

    public enum SpawnPlacement {
        ON_GROUND,
        IN_WATER,
        IN_LAVA
    }
}
