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
 * Serves as the foundational blueprint for injecting sophisticated custom entity definitions
 * into the standard Bukkit environment, strictly encapsulating behavioral goals, attributes, and precise spawn logic.
 *
 * @param <T> The native Bukkit living entity interface defining the fundamental morphological base representation.
 */
public class CustomEntity<T extends LivingEntity> implements Cloneable {

    /**
     * The persistent runtime universally unique identifier seamlessly binding the logic instance to an actively spawned entity in the world.
     */
    public UUID uuid = null;

    /**
     * The strictly validated namespace key registering and identifying this entity framework within the global registry.
     */
    private Key id;

    /**
     * The underlying native Bukkit EntityType strictly enforcing fundamental physical behaviors and model parameters.
     */
    private EntityType baseType;

    /**
     * The structural spawning category bracket explicitly determining the organic generation rules applied to this entity.
     */
    private SpawnCategory category;

    /**
     * The comprehensive configuration parameters determining autonomous structural generation logic bounds.
     */
    private SpawnSettings spawnSettings;

    /**
     * The ordered associative mapping detailing deterministic pathfinding intelligence routines.
     */
    private final Map<Integer, Function<T, Goal>> pathfinderGoals = new LinkedHashMap<>();

    /**
     * The ordered associative mapping detailing deterministic targeting intelligence routines.
     */
    private final Map<Integer, Function<T, Goal>> targetGoals = new LinkedHashMap<>();

    /**
     * The mapped configuration explicitly enforcing absolute statistical boundaries upon instantiation.
     */
    private final Map<Attribute, Double> attributes = new LinkedHashMap<>();

    /**
     * The mapped tracking infrastructure actively persisting specialized data components across execution states.
     */
    private ComponentMap componentMap;

    /**
     * The dynamically aggregated collection of additive and multiplicative mathematical modifiers actively manipulating base statistics.
     */
    private final Map<Attribute, List<AttributeModifier>> modifiers = new LinkedHashMap<>();

    /**
     * Instantiates a fully functional entity wrapper tying complex behavioral constraints strictly to a categorical definition.
     *
     * @param id        The strict namespace identifier formally registering this entity framework.
     * @param baseType  The underlying native Bukkit EntityType defining the core physical rendering capabilities natively safely.
     * @param category  The structural spawn bracket determining global organic generation limitations.
     */
    public CustomEntity(Key id, EntityType baseType, SpawnCategory category) {
        this.id = id;
        this.baseType = baseType;
        this.category = category;
    }

    /**
     * Embeds a fully deterministic pathfinding logic routine directly into the top-level behavioral hierarchy mapping.
     *
     * @param priority The strict numerical priority dynamically determining operational execution overrides (lower values guarantee higher precedence).
     * @param goal     The functional lambda supplier actively generating native NMS operational goal instances.
     */
    public void addGoal(int priority, Function<T, Goal> goal) {
        pathfinderGoals.put(priority, goal);
    }

    /**
     * Embeds a fully deterministic targeting logic routine defining aggressive or passive entity acquisition behaviors.
     *
     * @param priority The strict numerical priority dynamically determining operational execution overrides (lower values guarantee higher precedence).
     * @param goal     The functional lambda supplier actively generating native NMS operational goal instances.
     */
    public void addTargetGoal(int priority, Function<T, Goal> goal) {
        targetGoals.put(priority, goal);
    }

    /**
     * Overrides and explicitly modifies the foundational base statistical limits physically bounded to the entity upon instantiation execution.
     *
     * @param attribute The strict Bukkit attribute definition directly targeted for manipulation.
     * @param value     The absolute numerical limit to enforce natively upon the base entity architecture.
     */
    public void setAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }

    /**
     * Appends an active additive or multiplicative modifier functionally manipulating the base statistical limits indefinitely.
     *
     * @param attribute The strict Bukkit attribute definition directly targeted for manipulation.
     * @param modifier  The formulated mathematical operator payload modifying the statistic.
     */
    public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        modifiers.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    }

    /**
     * Caches the comprehensive procedural bounds guiding completely autonomous organic world generation operations.
     *
     * @param settings The formally configured environmental spawn constraint record.
     */
    public void setSpawnSettings(SpawnSettings settings) {
        this.spawnSettings = settings;
    }

    /**
     * Retrieves the structural and environmental constraints dictating autonomous procedural generation logic boundaries.
     *
     * @return The formally cached spawning parameters, or null entirely if generation is deliberately unsupported.
     */
    public @Nullable SpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    /**
     * Materializes the defined entity template directly into the physical world space immediately utilizing standard operational hooks.
     *
     * @param loc The precise Cartesian coordinate anchor designating the spawning origin point.
     */
    @SuppressWarnings("unchecked")
    public void spawn(Location loc) {
        spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }

    /**
     * Materializes the defined entity template completely into the physical world space, strictly citing a specific operational catalytic origin.
     *
     * @param loc    The precise Cartesian coordinate anchor designating the spawning origin point.
     * @param reason The strictly classified procedural origin formally initiating this spawning execution request.
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
     * Actively captures an already existing physical entity instantiation and forcibly overrides its internal operational state with this specific custom wrapper.
     *
     * @param entity The actively living physical entity reference targeted for behavioral capture.
     */
    public void spawn(T entity) {
        spawn(entity, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }

    /**
     * Actively captures an already existing physical entity instantiation and forcibly overrides its internal operational state, citing a specific operational cause.
     *
     * @param entity The actively living physical entity reference targeted for behavioral capture.
     * @param reason The strictly classified procedural origin formally initiating this behavioral takeover request.
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
     * Safely executes all standard spawning configuration pipelines natively utilizing a completely pre-resolved raw entity instance footprint.
     *
     * @param nativeEntity The underlying raw Bukkit entity instance functionally deployed by the internal chunk engine.
     */
    @SuppressWarnings("unchecked")
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
     * Safely applies a persistent data parameter masking natively into the actively tracked entity component mapping.
     *
     * @param component The fully structured data segment formally mapped into the persistent runtime environment.
     */
    public void setData(DataComponent<?> component) {
        componentMap.setData(component);
    }

    /**
     * Actively interrogates and retrieves dynamic persistent data presently mapped directly into the internal runtime footprint.
     *
     * @param type The definitive identification reference key explicitly requesting the data fragment.
     * @param <C>  The specifically explicit typed return format constraint restricting data bounds.
     * @return The safely retrieved data component, resolving cleanly into standard default boundaries if entirely absent.
     */
    public <C extends DataComponent<?>> C getData(DataComponentType<C> type) {
        return componentMap.getData(type);
    }

    /**
     * Safely evaluates whether a explicitly designated data segment is presently firmly bound to the active entity profile representation.
     *
     * @param type The definitive identification reference key explicitly requesting the data fragment.
     * @return True if the targeted data is actively bound within the current execution scope, otherwise explicitly false.
     */
    public boolean hasData(DataComponentType<?> type) {
        return componentMap.hasData(type);
    }

    /**
     * Destructively deletes an actively mapped component instance completely from the functional operational profile boundary.
     *
     * @param type The definitive identification reference key explicitly targeting the doomed data fragment.
     */
    public void unsetData(DataComponentType<?> type) {
        componentMap.removeData(type);
    }

    /**
     * Actively injects all properly registered internal intelligence logic arrays straight into the fundamental base Native Minecraft Server AI controller algorithms.
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
     * Sequentially iterates all internal mathematical property mappings and securely enforces all calculated statistical boundaries directly to the active physical instance state.
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
     * Dispatches internal localized component instantiation operations immediately synchronizing defined properties cleanly to the active living model architecture.
     */
    public void applyComponents() {
        componentMap = new ComponentMap(this);
    }

    /**
     * Retrieves the formalized structural namespace identifier directly mapping the definition to the central configuration registries.
     *
     * @return The specific, formally registered plugin namespace key.
     */
    public Key getId() {
        return id;
    }

    /**
     * Retrieves the strictly native morphological Bukkit EntityType securely bound seamlessly acting precisely generating correct native physical parameters natively.
     *
     * @return The structurally validated core API underlying target type enum defining entity limitations cleanly natively.
     */
    public EntityType getBaseType() {
        return baseType;
    }

    /**
     * Extracts an actively hooked interface cleanly interfacing the native Bukkit PersistentDataContainer internal implementation structure.
     *
     * @return The wrapped data container API payload wrapper, or strictly empty if currently completely disconnected from any active instantiation.
     */
    public Optional<PDCTag> getData() {
        if (uuid == null) return Optional.empty();

        LivingEntity entity = (LivingEntity) org.bukkit.Bukkit.getEntity(uuid);
        if (entity == null) return Optional.empty();

        PersistentDataContainer container = entity.getPersistentDataContainer();
        return Optional.of(new PDCTag(container));
    }

    /**
     * Deeply interrogates and cleanly isolates all underlying vanilla CustomData Native Binary Tag fragments dynamically directly from active underlying server engine memory banks.
     *
     * @return A thoroughly unified, modifiable compound data map structure encompassing entity internals.
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
     * Actively synchronizes and completely overwrites the local virtual NBT metadata maps directly deep into the central engine memory block handling runtime rendering.
     *
     * @param container The active modified state container dictating the complete required serialization formats.
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
     * Securely polls the core Bukkit thread architecture actively attempting to safely locate and bind the live physical entity representation executing the custom logic.
     *
     * @return The actively functional native API wrapper interface encapsulating runtime states.
     */
    @SuppressWarnings("unchecked")
    public Optional<T> getBaseEntity() {
        return Optional.ofNullable((T) Bukkit.getEntity(uuid));
    }

    /**
     * Identifies the explicitly defined categorical constraints strictly governing procedural distribution and background spawn deployment mechanics.
     *
     * @return The natively associated logical grouping bracket spanning general distribution constraints.
     */
    public SpawnCategory getCategory() {
        return category;
    }

    /**
     * Internal lifecycle hook triggered precisely upon the immediate conclusion of a successful entity deployment operation into physical space.
     */
    public void onSpawn() {}

    /**
     * Internal lifecycle hook triggered precisely upon the absolute termination of the entity's functional lifespan terminating physical rendering.
     *
     * @param event The Bukkit death event payload encapsulating the final functional runtime parameters.
     * @return The explicit action result dictating event finalization overrides.
     */
    public ActionResult onDeath(EntityDeathEvent event) { return ActionResult.PASS; }

    /**
     * Internal lifecycle hook triggered precisely when the chunk enclosing the specific physical entity formally vacates active server memory banks.
     */
    public void onUnload() {}

    /**
     * Internal lifecycle hook triggered precisely when the physical entity is actively reconstructed directly from persistent server memory disk sectors.
     */
    public void onLoad() {}

    /**
     * Translates an arbitrary base Bukkit entity reference rapidly back into a safely managed active custom logic shell wrapper environment.
     *
     * @param entity The living physical entity reference queried against the active entity registry system.
     * @return The actively functional customized wrapper interface, or entirely null if strictly unmanaged by the plugin framework.
     */
    public static CustomEntity<? extends LivingEntity> resolve(org.bukkit.entity.Entity entity) {
        return EntityManager.get(entity.getUniqueId());
    }

    /**
     * Executes a precise deep clone operation fully replicating all intrinsic properties, goals, attributes, and operational parameters safely into an entirely independent framework shell.
     *
     * @return A comprehensively cloned distinct copy of the initial customized entity definition framework.
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
     * Structurally encapsulates all conditional logic rules, topographical filters, and probability boundaries explicitly governing procedural world generation spawning mechanics.
     */
    public static class SpawnSettings {

        /** The base functional probability weight factoring into algorithmic generation distributions. */
        public int weight;

        /** The absolute minimal guaranteed instantiation boundary dictating isolated pack clustering numbers. */
        public int minPack;

        /** The absolute maximal permitted instantiation boundary dictating isolated pack clustering numbers. */
        public int maxPack;

        /** The foundational floor limiting the vertical spatial height allowed for autonomous generation execution. */
        public int minY = ServerLevel.MIN_ENTITY_SPAWN_Y;

        /** The upper structural ceiling limiting the vertical spatial height allowed for autonomous generation execution. */
        public int maxY = ServerLevel.MAX_ENTITY_SPAWN_Y;

        /** The absolute minimal registered photoluminesence boundary determining valid location selection. */
        public int minLight = 0;

        /** The absolute maximal registered photoluminesence boundary determining valid location selection. */
        public int maxLight = 15;

        /** Dictates an absolute systemic requirement ensuring native block skylight access reads precisely zero. */
        public boolean requireSkyDarkness;

        /** Dictates an absolute systemic requirement ensuring an unobstructed vertical trace completely to the world height limit. */
        public boolean requireSkyAccess;

        /** Establishes the core geometrical medium defining acceptable locational bounding box insertions. */
        public SpawnPlacement placement;

        /** Actively bounds structural path tracing targeting routines to definitively locate optimal top-level spawning coordinates. */
        public HeightMap heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES;

        /** Defines the extremely specific restricted array of allowed biomes authorizing the completion of generation checks. */
        public Set<NamespacedKey> biomes = new HashSet<>();

        /** Provides an open external lambda hook seamlessly evaluating final bespoke logical requirements preventing spatial deployment. */
        public BiPredicate<World, Location> canSpawn;

        /**
         * Systematically instantiates the completely restricted settings wrapper defining standard deployment boundaries.
         *
         * @param weight    The assigned probability distribution fraction weight.
         * @param minPack   The lowest valid instantiation spawn cluster parameter.
         * @param maxPack   The uppermost valid instantiation spawn cluster parameter.
         * @param placement The fundamental geometric collision rules framing locational insertion capabilities.
         */
        private SpawnSettings(int weight, int minPack, int maxPack, SpawnPlacement placement) {
            this.weight = Math.max(0, weight);
            this.minPack = Math.max(1, minPack);
            this.maxPack = Math.max(this.minPack, maxPack);
            this.placement = placement;
        }

        /**
         * Initializes a structural builder pipeline sequentially generating comprehensive configurations ensuring fluid integration.
         *
         * @return An entirely clean state configuration builder framework payload.
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Orchestrates the fluid systematic chaining defining individual generation parameters rapidly culminating within an immutable settings framework.
         */
        public static class Builder {

            /** Internal temporary weight parameter cache. */
            private int weight = 1;

            /** Internal temporary minimum pack size parameter cache. */
            private int minPack = 1;

            /** Internal temporary maximum pack size parameter cache. */
            private int maxPack = 1;

            /** Internal temporary minimum structural Y bounds parameter cache. */
            private int minY = ServerLevel.MIN_ENTITY_SPAWN_Y;

            /** Internal temporary maximum structural Y bounds parameter cache. */
            private int maxY = ServerLevel.MAX_ENTITY_SPAWN_Y;

            /** Internal temporary minimum luminance bounds parameter cache. */
            private int minLight = 0;

            /** Internal temporary maximum luminance bounds parameter cache. */
            private int maxLight = 15;

            /** Internal temporary required darkness parameter cache. */
            private boolean requireSkyDarkness;

            /** Internal temporary required sky tracing parameter cache. */
            private boolean requireSkyAccess;

            /** Internal temporary collision and medium state requirement parameter cache. */
            private SpawnPlacement placement = SpawnPlacement.ON_GROUND;

            /** Internal temporary target height mapping parameter cache. */
            private HeightMap heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES;

            /** Internal temporary accepted biome keys array parameter cache. */
            private final Set<NamespacedKey> biomes = new HashSet<>();

            /** Internal temporary lambda filter payload parameter cache. */
            private BiPredicate<World, Location> canSpawn;

            /**
             * Sets the fundamental mathematical distribution probability actively increasing frequency during selection routines.
             *
             * @param weight The formulated integer scaling factor applied to distribution tables.
             * @return The current unbroken chained builder phase.
             */
            public Builder weight(int weight) { this.weight = weight; return this; }

            /**
             * Defines the strictly bounded minimum and maximal instantiation cluster values resulting upon successful localization selections.
             *
             * @param min The absolute lowest volume bounding spawn clusters natively generated.
             * @param max The absolute highest volume bounding spawn clusters natively generated.
             * @return The current unbroken chained builder phase.
             */
            public Builder pack(int min, int max) { this.minPack = min; this.maxPack = max; return this; }

            /**
             * Specifically designates the underlying spatial geometrical structure strictly required accommodating the bounding boxes footprint natively.
             *
             * @param placement The mandated operational placement mode formatting collision checks correctly.
             * @return The current unbroken chained builder phase.
             */
            public Builder placement(SpawnPlacement placement) { this.placement = placement; return this; }

            /**
             * Enforces absolute physical elevation coordinate restrictions bounding the specific allowed limits of execution attempts natively.
             *
             * @param minY The extreme lowest accepted geographical location point limit bounds restricting placement.
             * @param maxY The extreme highest accepted geographical location point limit bounds restricting placement.
             * @return The current unbroken chained builder phase.
             */
            public Builder heightRange(int minY, int maxY) { this.minY = minY; this.maxY = maxY; return this; }

            /**
             * Installs specific topological tracing instructions ensuring raycasting bounds calculate structural elevation maps perfectly utilizing predefined engine states.
             *
             * @param map The strict predefined Native Minecraft Server topographical tracing execution map mode.
             * @return The current unbroken chained builder phase.
             */
            public Builder heightMap(HeightMap map) { this.heightMap = map; return this; }

            /**
             * Implements the strict required ambient environmental photoluminesence constraints determining fundamental valid physical spawn deployment conditions natively.
             *
             * @param min The extreme minimum allowable illumination emission detection bounds dictating execution operations.
             * @param max The extreme maximum allowable illumination emission detection bounds dictating execution operations.
             * @return The current unbroken chained builder phase.
             */
            public Builder light(int min, int max) { this.minLight = min; this.maxLight = max; return this; }

            /**
             * Flips strict internal execution parameters bounding operational capacity to inherently restrict physical manifestations actively towards nocturnal or subterranean lighting contexts exclusively.
             *
             * @return The current unbroken chained builder phase.
             */
            public Builder nightOnly() { this.requireSkyDarkness = true; this.maxLight = Math.min(this.maxLight, 7); return this; }

            /**
             * Implements an absolute physical geometrical requirement enforcing unobstructed vertical path traces verifying clear exposures to open upper atmospheric spatial boundaries explicitly.
             *
             * @return The current unbroken chained builder phase.
             */
            public Builder requireSkyAccess() { this.requireSkyAccess = true; return this; }

            /**
             * Specifically links a completely restricted external procedural biome identifier natively into the operative generation allowed execution array natively ensuring strict deployment zones.
             *
             * @param biome The targeted strictly registered namespaced mapping identifier native within the engine bounds.
             * @return The current unbroken chained builder phase.
             */
            public Builder biome(NamespacedKey biome) { this.biomes.add(biome); return this; }

            /**
             * Concatenates an extensive structured array bounding procedural execution identifiers natively directly into the allowed operational execution array seamlessly natively expanding deployment boundaries comprehensively.
             *
             * @param biomes The full structural list enclosing active registered namespaced identifier bounds.
             * @return The current unbroken chained builder phase.
             */
            public Builder biomes(Collection<NamespacedKey> biomes) { this.biomes.addAll(biomes); return this; }

            /**
             * Implements a totally unbounded execution lambda parameter cleanly inserting highly bespoke operational programmatic requirements restricting standard native spawn algorithms fluidly.
             *
             * @param predicate The fully programmed custom functional structural boundary evaluation script evaluating dynamic coordinate bounds precisely.
             * @return The current unbroken chained builder phase.
             */
            public Builder canSpawn(BiPredicate<World, Location> predicate) { this.canSpawn = predicate; return this; }

            /**
             * Swiftly adjusts configuration templates mapping strictly to standardized terrestrial generation boundaries implementing standard gravity and collision states dynamically.
             *
             * @return The current unbroken chained builder phase.
             */
            public Builder groundMob() { this.placement = SpawnPlacement.ON_GROUND; this.heightMap = HeightMap.MOTION_BLOCKING_NO_LEAVES; return this; }

            /**
             * Swiftly adjusts configuration templates mapping strictly to standardized aquatic generation boundaries implementing standard displacement and oceanic height map collision states dynamically natively.
             *
             * @return The current unbroken chained builder phase.
             */
            public Builder waterMob() { this.placement = SpawnPlacement.IN_WATER; this.heightMap = HeightMap.OCEAN_FLOOR; return this; }

            /**
             * Swiftly adjusts configuration templates mapping strictly to standardized igneous generation boundaries implementing standard lava displacement collision states actively natively seamlessly.
             *
             * @return The current unbroken chained builder phase.
             */
            public Builder lavaMob() { this.placement = SpawnPlacement.IN_LAVA; this.heightMap = HeightMap.MOTION_BLOCKING; return this; }

            /**
             * Translates the currently staged internal mutable structural parameters completely into an inherently safe immutable finalized physical execution settings record utilized fundamentally by generation algorithms natively.
             *
             * @return The entirely fully integrated formal native entity functional settings constraints snapshot completely finalized.
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
     * Enumerates the strict environmental collision geometry bounds utilized universally to map entity footprints into defined physical mediums flawlessly natively.
     */
    public enum SpawnPlacement {

        /** Defines the physical geometry completely executing utilizing standard opaque horizontal collision bounding box intersections specifically natively. */
        ON_GROUND,

        /** Defines the physical geometry completely executing utilizing fully liquid water-based completely unobstructed block intersection limits natively securely. */
        IN_WATER,

        /** Defines the physical geometry completely executing utilizing fully liquid lava-based strictly heat-resistant block intersection restrictions comprehensively natively. */
        IN_LAVA
    }
}