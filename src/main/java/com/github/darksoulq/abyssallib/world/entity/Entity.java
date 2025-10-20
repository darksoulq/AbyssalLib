package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.common.util.PDCTag;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.EventBus;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntitySpawnEvent;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.abyssallib.world.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.entity.internal.NMSGoalHandler;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Entity<T extends LivingEntity> implements Cloneable {
    public UUID uuid = null;
    private Identifier id;
    private Class<T> baseClass;
    private SpawnCategory category;

    private final Map<Integer, Function<T, Goal>> pathfinderGoals = new LinkedHashMap<>();
    private final Map<Integer, Function<T, Goal>> targetGoals = new LinkedHashMap<>();
    private final Map<Attribute, Double> attributes = new LinkedHashMap<>();
    private ComponentMap componentMap;
    private final Map<Attribute, List<AttributeModifier>> modifiers = new LinkedHashMap<>();
    private final List<BiPredicate<World, Location>> spawnConditions = new ArrayList<>();
    private static final Map<Biome, List<SpawnEntry>> spawnTable = new HashMap<>();

    public Entity(Identifier id, Class<T> baseClass, SpawnCategory category) {
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

    public void addSpawnCondition(BiPredicate<World, Location> cond) {
        spawnConditions.add(cond);
    }
    public void addSpawnWeight(Biome biome, float weight, int minGroup, int maxGroup) {
        spawnTable.computeIfAbsent(biome, k -> new ArrayList<>()).add(new SpawnEntry(id, weight, minGroup, maxGroup));
    }

    public void setAttribute(Attribute attribute, double value) {
        attributes.put(attribute, value);
    }
    public void addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        modifiers.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    }

    public void spawn(Location loc) {
        spawn(loc, EntitySpawnEvent.SpawnReason.PLUGIN);
    }
    public void spawn(Location loc, EntitySpawnEvent.SpawnReason reason) {
        if (uuid != null) return;
        T entity = loc.getWorld().spawn(loc, baseClass);
        this.uuid = entity.getUniqueId();

        EntitySpawnEvent event = EventBus.post(new EntitySpawnEvent(this, reason));
        if (event.isCancelled()) {
            entity.remove();
            return;
        }

        onSpawn();
        applyGoals();
        applyAttributes();
        applyComponents();

        AbyssalLib.LOGGER.info("Entity spawned at " + loc);
        EntityManager.add(this);
    }
    public void spawn(T entity) {
        spawn(entity, EntitySpawnEvent.SpawnReason.PLUGIN);
    }
    public void spawn(T entity, EntitySpawnEvent.SpawnReason reason) {
        if (uuid != null) return;
        this.uuid = entity.getUniqueId();

        EntitySpawnEvent event = EventBus.post(new EntitySpawnEvent(this, reason));
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
    public DataComponent<?> getData(Identifier id) {
        return componentMap.getData(id);
    }
    public DataComponent<?> getData(DataComponentType type) {
        return componentMap.getData(type);
    }
    public <T extends DataComponent<?>> DataComponent<?> getData(Class<T> clazz) {
        return clazz.cast(componentMap.getData(clazz));
    }
    public boolean hasData(Identifier id) {
        return componentMap.hasData(id);
    }
    public boolean hasData(DataComponentType type) {
        return componentMap.hasData(type);
    }
    public void unsetData(Identifier id) {
        componentMap.removeData(id);
    }
    public void unsetData(Class<? extends DataComponent> clazz) {
        componentMap.removeData(clazz);
    }
    public <T extends DataComponent<?>> boolean hasData(Class<T> clazz) {
        return componentMap.hasData(clazz);
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

    public List<BiPredicate<World, Location>> getSpawnConditions() {
        return spawnConditions;
    }

    public void onSpawn() {}
    public ActionResult onDeath(EntityDeathEvent event) { return ActionResult.PASS; }
    public void onUnload() {}
    public void onLoad() {}

    public static Entity<? extends LivingEntity> resolve(org.bukkit.entity.Entity entity) {
        return EntityManager.get(entity.getUniqueId());
    }
    public static EntityEntry getWeighedSpawnEntry(Biome biome, SpawnCategory category) {
        Map<SpawnEntry, Entity<? extends LivingEntity>> entities = new HashMap<>();
        List<SpawnEntry> entries = spawnTable.get(biome);
        if (entries == null) return null;
        entries.forEach(se -> {
            Entity<? extends LivingEntity> entity = Registries.ENTITIES.get(se.id.toString());
            if (entity == null) return;
            if (!entity.category.equals(category)) return;
            entities.put(se, entity);
        });
        if (entities.isEmpty()) return null;

        double totalWeight = entities.keySet().stream().mapToDouble(e -> e.weight).sum();
        double r = EntityManager.rand.nextDouble(totalWeight);
        double running = 0;

        for (SpawnEntry entry : entities.keySet()) {
            running += entry.weight;
            if (r < running) return new EntityEntry(entry, entities.get(entry));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entity<T> clone() throws CloneNotSupportedException {
        Entity<T> copy = (Entity<T>) super.clone();

        copy.id = id;
        copy.baseClass = baseClass;
        copy.category = category;
        this.pathfinderGoals.forEach(copy::addGoal);
        this.targetGoals.forEach(copy::addTargetGoal);
        this.attributes.forEach(copy::setAttribute);
        copy.spawnConditions.addAll(this.spawnConditions);

        this.modifiers.forEach((attr, list) ->
                list.forEach(mod -> copy.addAttributeModifier(attr, mod))
        );

        return copy;
    }

    public record SpawnEntry(Identifier id, float weight, int minGroup, int maxGroup) {}
    public record EntityEntry(SpawnEntry entry, Entity<? extends LivingEntity> entity) {}
}
