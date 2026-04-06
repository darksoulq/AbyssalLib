package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.entity.internal.NMSGoalHandler;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.util.CTag;
import com.github.darksoulq.abyssallib.world.util.PDCTag;
import net.kyori.adventure.key.Key;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Represents a fully configurable wrapper around a Bukkit {@link LivingEntity},
 * providing extended behavior, attributes, data components, and lifecycle control.
 *
 * <p>This abstraction enables:
 * <ul>
 *     <li>Custom AI goals via {@link Goal}</li>
 *     <li>Attribute and modifier control</li>
 *     <li>Persistent data via {@link ComponentMap}, {@link PDCTag}, and {@link CTag}</li>
 *     <li>Custom spawning pipelines and lifecycle hooks</li>
 *     <li>Integration with internal registries and event systems</li>
 * </ul>
 *
 * @param <T> the concrete {@link LivingEntity} type backing this custom entity
 */
public class CustomEntity<T extends LivingEntity> implements Cloneable {

    /**
     * Unique identifier linking this wrapper to a live entity instance.
     */
    public UUID uuid = null;

    /**
     * Unique registry identifier for this entity.
     */
    private Key id;

    /**
     * Base Bukkit {@link EntityType} used for spawning.
     */
    private EntityType baseType;

    /**
     * Spawn category defining grouping and distribution rules.
     */
    private SpawnCategory category;

    /**
     * Optional spawn configuration settings.
     */
    private SpawnSettings spawnSettings;

    /**
     * Registered pathfinding goals mapped by priority.
     */
    private final Map<Integer, Function<T, Goal>> pathfinderGoals = new LinkedHashMap<>();

    /**
     * Registered targeting goals mapped by priority.
     */
    private final Map<Integer, Function<T, Goal>> targetGoals = new LinkedHashMap<>();

    /**
     * Base attribute values applied on spawn.
     */
    private final Map<Attribute, Double> attributes = new LinkedHashMap<>();

    /**
     * Component map storing persistent custom data.
     */
    private ComponentMap componentMap;

    /**
     * Attribute modifiers applied on spawn.
     */
    private final Map<Attribute, List<AttributeModifier>> modifiers = new LinkedHashMap<>();

    /**
     * Creates a new custom entity definition.
     *
     * @param id       the unique registry identifier
     * @param baseType the base {@link EntityType}
     * @param category the spawn category
     */
    public CustomEntity(Key id, EntityType baseType, SpawnCategory category) {
        this.id = id;
        this.baseType = baseType;
        this.category = category;
    }

    /**
     * Registers a pathfinding goal.
     *
     * @param priority execution priority (lower = higher priority)
     * @param goal     goal factory
     */
    public void addGoal(int priority, Function<T, Goal> goal) {
        pathfinderGoals.put(priority, goal);
    }

    /**
     * Registers a targeting goal.
     *
     * @param priority execution priority
     * @param goal     goal factory
     */
    public void addTargetGoal(int priority, Function<T, Goal> goal) {
        targetGoals.put(priority, goal);
    }

    /**
     * Sets a base attribute value.
     *
     * @param attribute the attribute
     * @param value     the value
     */
    public void setAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }

    /**
     * Adds an attribute modifier.
     *
     * @param attribute the attribute
     * @param modifier  the modifier
     */
    public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        modifiers.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    }

    /**
     * Sets spawn configuration.
     *
     * @param settings the spawn settings
     */
    public void setSpawnSettings(SpawnSettings settings) {
        this.spawnSettings = settings;
    }

    /**
     * Gets spawn configuration.
     *
     * @return the spawn settings or {@code null}
     */
    public @Nullable SpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    /**
     * Spawns this entity at a location using plugin reason.
     *
     * @param loc the spawn location
     */
    public void spawn(Location loc) {
        spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }

    /**
     * Spawns this entity at a location.
     *
     * @param loc    the spawn location
     * @param reason the spawn reason
     */
    @SuppressWarnings("unchecked")
    public void spawn(Location loc, CustomEntitySpawnEvent.SpawnReason reason) {
        if (uuid != null) return;
        T entity = (T) loc.getWorld().spawnEntity(loc, baseType);
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

    /**
     * Attaches this wrapper to an existing entity.
     *
     * @param entity the entity
     */
    public void spawn(T entity) {
        spawn(entity, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }

    /**
     * Attaches this wrapper to an existing entity.
     *
     * @param entity the entity
     * @param reason the spawn reason
     */
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

    /**
     * Initializes this wrapper from a raw entity instance.
     *
     * @param nativeEntity the entity
     */
    public void spawnFromInstance(Entity nativeEntity) {
        if (uuid != null) return;
        this.uuid = nativeEntity.getUniqueId();

        CustomEntitySpawnEvent event = EventBus.post(new CustomEntitySpawnEvent(this, CustomEntitySpawnEvent.SpawnReason.NATURAL));
        if (event.isCancelled()) return;

        onSpawn();
        applyGoals();
        applyAttributes();
        applyComponents();
        EntityManager.add(this);
    }

    /**
     * Sets a data component.
     *
     * @param component the component
     */
    public void setData(DataComponent<?> component) {
        componentMap.setData(component);
    }

    /**
     * Gets a data component.
     *
     * @param type the component type
     * @param <C>  component type
     * @return the component
     */
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        return componentMap.getData(type);
    }

    /**
     * Checks if a component exists.
     *
     * @param type the component type
     * @return true if present
     */
    public boolean hasData(DataComponentType<?> type) {
        return componentMap.hasData(type);
    }

    /**
     * Removes a component.
     *
     * @param type the component type
     */
    public void unsetData(DataComponentType<?> type) {
        componentMap.removeData(type);
    }

    /**
     * Applies all registered goals.
     */
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

    /**
     * Applies attributes and modifiers.
     */
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

    /**
     * Initializes the component map.
     */
    public void applyComponents() {
        componentMap = new ComponentMap(this);
    }

    /**
     * @return the entity identifier
     */
    public Key getId() {
        return id;
    }

    /**
     * @return the base entity type
     */
    public EntityType getBaseType() {
        return baseType;
    }

    /**
     * @return the component map
     */
    public ComponentMap getComponentMap() {
        return componentMap;
    }

    /**
     * @return attribute map
     */
    public Map<Attribute, Double> getAttributes() {
        return attributes;
    }

    /**
     * @return modifiers map
     */
    public Map<Attribute, List<AttributeModifier>> getModifiers() {
        return modifiers;
    }

    /**
     * @return target goals
     */
    public Map<Integer, Function<T, Goal>> getTargetGoals() {
        return targetGoals;
    }

    /**
     * @return pathfinder goals
     */
    public Map<Integer, Function<T, Goal>> getPathfinderGoals() {
        return pathfinderGoals;
    }

    /**
     * Gets wrapped persistent data container.
     *
     * @return optional PDC wrapper
     */
    public Optional<PDCTag> getData() {
        if (uuid == null) return Optional.empty();

        LivingEntity entity = (LivingEntity) org.bukkit.Bukkit.getEntity(uuid);
        if (entity == null) return Optional.empty();

        PersistentDataContainer container = entity.getPersistentDataContainer();
        return Optional.of(new PDCTag(container));
    }

    /**
     * Gets underlying NBT custom data.
     *
     * @return custom tag or null
     */
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

    /**
     * Sets underlying NBT custom data.
     *
     * @param container the tag
     */
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

    /**
     * Resolves the live entity.
     *
     * @return optional base entity
     */
    @SuppressWarnings("unchecked")
    public Optional<T> getBaseEntity() {
        return Optional.ofNullable((T) Bukkit.getEntity(uuid));
    }

    /**
     * @return spawn category
     */
    public SpawnCategory getCategory() {
        return category;
    }

    /**
     * Called after spawn.
     */
    public void onSpawn() {}

    /**
     * Called on entity death.
     *
     * @param event the death event
     * @return action result
     */
    public ActionResult onDeath(EntityDeathEvent event) { return ActionResult.PASS; }

    /**
     * Called when entity unloads.
     */
    public void onUnload() {}

    /**
     * Called when entity loads.
     */
    public void onLoad() {}

    /**
     * Resolves a custom entity wrapper.
     *
     * @param entity the entity
     * @return custom entity or null
     */
    public static CustomEntity<? extends LivingEntity> resolve(org.bukkit.entity.Entity entity) {
        return EntityManager.get(entity.getUniqueId());
    }

    /**
     * Creates a deep clone of this entity definition.
     *
     * @return cloned entity
     */
    @SuppressWarnings("unchecked")
    @Override
    public CustomEntity<T> clone() {
        try {
            CustomEntity<T> copy = (CustomEntity<T>) super.clone();

            copy.id = id;
            copy.baseType = baseType;
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

    /**
     * Defines spawn configuration.
     */
    public static class SpawnSettings {

        /** Spawn weight. */
        public int weight;

        /** Minimum pack size. */
        public int minPack;

        /** Maximum pack size. */
        public int maxPack;

        /** Minimum Y level. */
        public int minY = ServerLevel.MIN_ENTITY_SPAWN_Y;

        /** Maximum Y level. */
        public int maxY = ServerLevel.MAX_ENTITY_SPAWN_Y;

        /** Minimum light level. */
        public int minLight = 0;

        /** Maximum light level. */
        public int maxLight = 15;

        /** Requires darkness. */
        public boolean requireSkyDarkness;

        /** Requires sky access. */
        public boolean requireSkyAccess;

        /** Spawn placement type. */
        public SpawnPlacement placement;

        /** Heightmap type. */
        public HeightMap heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES;

        /** Allowed biomes. */
        public Set<NamespacedKey> biomes = new HashSet<>();

        /** Custom spawn predicate. */
        public BiPredicate<World, Location> canSpawn;

        /**
         * Creates new spawn settings.
         *
         * @param weight    spawn weight
         * @param minPack   min pack
         * @param maxPack   max pack
         * @param placement placement type
         */
        private SpawnSettings(int weight, int minPack, int maxPack, SpawnPlacement placement) {
            this.weight = Math.max(0, weight);
            this.minPack = Math.max(1, minPack);
            this.maxPack = Math.max(this.minPack, maxPack);
            this.placement = placement;
        }

        /**
         * @return new builder
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder for {@link SpawnSettings}.
         */
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

            /** @param weight spawn weight */
            public Builder weight(int weight) { this.weight = weight; return this; }

            /** @param min min pack @param max max pack */
            public Builder pack(int min, int max) { this.minPack = min; this.maxPack = max; return this; }

            /** @param placement placement */
            public Builder placement(SpawnPlacement placement) { this.placement = placement; return this; }

            /** @param minY min Y @param maxY max Y */
            public Builder heightRange(int minY, int maxY) { this.minY = minY; this.maxY = maxY; return this; }

            /** @param map heightmap */
            public Builder heightMap(HeightMap map) { this.heightMap = map; return this; }

            /** @param min min light @param max max light */
            public Builder light(int min, int max) { this.minLight = min; this.maxLight = max; return this; }

            /** Enables night-only spawning */
            public Builder nightOnly() { this.requireSkyDarkness = true; this.maxLight = Math.min(this.maxLight, 7); return this; }

            /** Requires sky access */
            public Builder requireSkyAccess() { this.requireSkyAccess = true; return this; }

            /** @param biome biome key */
            public Builder biome(NamespacedKey biome) { this.biomes.add(biome); return this; }

            /** @param biomes biome keys */
            public Builder biomes(Collection<NamespacedKey> biomes) { this.biomes.addAll(biomes); return this; }

            /** @param predicate spawn predicate */
            public Builder canSpawn(BiPredicate<World, Location> predicate) { this.canSpawn = predicate; return this; }

            /** Ground preset */
            public Builder groundMob() { this.placement = SpawnPlacement.ON_GROUND; this.heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES; return this; }

            /** Water preset */
            public Builder waterMob() { this.placement = SpawnPlacement.IN_WATER; this.heightMap = HeightMap.OCEAN_FLOOR; return this; }

            /** Lava preset */
            public Builder lavaMob() { this.placement = SpawnPlacement.IN_LAVA; this.heightMap = HeightMap.MOTION_BLOCKING; return this; }

            /**
             * Builds settings.
             *
             * @return settings
             */
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

    /**
     * Defines spawn placement type.
     */
    public enum SpawnPlacement {

        /** Ground placement */
        ON_GROUND,

        /** Water placement */
        IN_WATER,

        /** Lava placement */
        IN_LAVA
    }
}