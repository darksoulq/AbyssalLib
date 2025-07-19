package com.github.darksoulq.abyssallib.world.level.entity;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.event.custom.entity.EntitySpawnEvent;
import com.github.darksoulq.abyssallib.world.level.data.Identifier;
import com.github.darksoulq.abyssallib.world.level.data.PDCTag;
import com.github.darksoulq.abyssallib.world.level.entity.internal.EntityManager;
import com.github.darksoulq.abyssallib.world.level.entity.internal.NMSGoalHandler;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class Entity<T extends LivingEntity> implements Cloneable {
    public UUID uuid;
    private Identifier id;
    private Class<T> baseClass;

    private final Map<Integer, Goal> pathfinderGoals = new LinkedHashMap<>();
    private final Map<Integer, Goal> targetGoals = new LinkedHashMap<>();
    private final Map<Attribute, Double> attributes = new LinkedHashMap<>();
    private final Map<Attribute, List<AttributeModifier>> modifiers = new LinkedHashMap<>();

    public Entity(Identifier id, Class<T> baseClass) {
        this.id = id;
        this.baseClass = baseClass;
    }

    public void addGoal(int priority, Goal goal) {
        pathfinderGoals.put(priority, goal);
    }
    public void addTargetGoal(int priority, Goal goal) {
        targetGoals.put(priority, goal);
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

        EntitySpawnEvent event = AbyssalLib.EVENT_BUS
                .post(new EntitySpawnEvent(this, reason));
        if (event.isCancelled()) {
            entity.remove();
            return;
        }

        applyGoals(entity);
        applyAttributes(entity);
    }
    public void spawn(LivingEntity entity) {
        spawn(entity, EntitySpawnEvent.SpawnReason.PLUGIN);
    }
    public void spawn(LivingEntity entity, EntitySpawnEvent.SpawnReason reason) {
        if (uuid != null) return;
        this.uuid = entity.getUniqueId();

        EntitySpawnEvent event = AbyssalLib.EVENT_BUS
                .post(new EntitySpawnEvent(this, reason));
        if (event.isCancelled()) return;

        applyGoals(entity);
        applyAttributes(entity);
    }

    public void applyGoals(LivingEntity entity) {
        NMSGoalHandler.clearGoals(entity);

        pathfinderGoals.forEach((priority, goal) ->
                NMSGoalHandler.addGoal(entity, goal, priority)
        );

        targetGoals.forEach((priority, goal) ->
                NMSGoalHandler.addTargetGoal(entity, goal, priority)
        );
    }
    public void applyAttributes(LivingEntity entity) {
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

    public static Entity<? extends LivingEntity> from(org.bukkit.entity.Entity entity) {
        return EntityManager.get(entity.getUniqueId());
    }

    @Override
    public Entity<T> clone() throws CloneNotSupportedException {
        Entity<T> copy = (Entity<T>) super.clone();

        copy.id = id;
        copy.baseClass = baseClass;
        this.pathfinderGoals.forEach(copy::addGoal);
        this.targetGoals.forEach(copy::addTargetGoal);
        this.attributes.forEach(copy::setAttribute);

        this.modifiers.forEach((attr, list) ->
                list.forEach(mod -> copy.addAttributeModifier(attr, mod))
        );

        return copy;
    }
}
