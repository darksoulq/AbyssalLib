package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a serialized snapshot of an entity, including its type and associated data.
 *
 * <p>This abstraction supports both vanilla {@link EntityType} instances and
 * {@link CustomEntity} definitions, allowing entities to be captured, stored,
 * and later reconstructed in different contexts.</p>
 *
 * <p>All data is stored in a format-agnostic representation using {@link DynamicOps}.</p>
 */
public class SavedEntity {

    /**
     * The underlying entity type representation.
     *
     * <p>This may either be a vanilla {@link EntityType} or a {@link CustomEntity}.</p>
     */
    private final Either<EntityType, CustomEntity<? extends LivingEntity>> type;

    /**
     * Raw serialized entity data.
     */
    private final Object rawData;

    /**
     * Dynamic operations instance used for encoding and decoding.
     */
    private final DynamicOps<?> ops;

    /**
     * Constructs a new saved entity instance.
     *
     * @param type    the entity type representation
     * @param rawData the serialized data payload
     * @param ops     the dynamic operations instance
     * @param <D>     the encoded data type
     */
    public <D> SavedEntity(Either<EntityType, CustomEntity<? extends LivingEntity>> type, D rawData, DynamicOps<D> ops) {
        this.type = type;
        this.rawData = rawData;
        this.ops = ops;
    }

    /**
     * Creates a {@link SavedEntity} from a live Bukkit {@link Entity}.
     *
     * <p>If the entity is associated with a {@link CustomEntity}, its identifier is stored.
     * Otherwise, the vanilla entity type is recorded.</p>
     *
     * @param entity the source entity
     * @param ops    the dynamic operations instance
     * @param <D>    the encoded data type
     * @return a new saved entity instance
     */
    public static <D> SavedEntity create(Entity entity, DynamicOps<D> ops) {
        CustomEntity<?> custom = CustomEntity.resolve(entity);
        Map<D, D> map = EntityAdapter.save(ops, entity);

        if (custom != null) {
            map.put(ops.createString("id"), Codecs.STRING.encode(ops, custom.getId().asString()));
            return new SavedEntity(Either.right(custom), ops.createMap(map), ops);
        } else {
            map.put(ops.createString("id"), Codecs.STRING.encode(ops, "minecraft:" + entity.getType().name().toLowerCase()));
            return new SavedEntity(Either.left(entity.getType()), ops.createMap(map), ops);
        }
    }

    /**
     * Retrieves the entity type representation.
     *
     * @return the {@link Either} containing either a vanilla type or custom entity
     */
    public Either<EntityType, CustomEntity<? extends LivingEntity>> getType() {
        return type;
    }

    /**
     * Retrieves the raw serialized data.
     *
     * @return the raw data object
     */
    public Object getRawData() {
        return rawData;
    }

    /**
     * Spawns this entity at a location using plugin spawn reason.
     *
     * @param loc the spawn location
     * @return the spawned entity
     */
    public Entity spawn(Location loc) {
        return spawn(loc, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }

    /**
     * Spawns this entity at a location.
     *
     * @param loc    the spawn location
     * @param reason the spawn reason
     * @return the spawned entity
     */
    public Entity spawn(Location loc, CustomEntitySpawnEvent.SpawnReason reason) {
        if (type.right().isPresent()) {
            CustomEntity<?> custom = type.right().get().clone();
            custom.spawn(loc, reason);
            Entity entity = custom.getBaseEntity().orElse(null);
            if (entity != null) applyData(entity);
            return entity;
        } else {
            Entity entity = loc.getWorld().spawnEntity(loc, type.left().get());
            applyData(entity);
            return entity;
        }
    }

    /**
     * Spawns this entity in a {@link WorldGenAccess} context.
     *
     * @param level the world generation access
     * @param loc   the spawn location
     * @return the spawned entity
     */
    public Entity spawn(WorldGenAccess level, Location loc) {
        if (type.right().isPresent()) {
            CustomEntity<?> custom = type.right().get().clone();
            level.addEntity(loc.getX(), loc.getY(), loc.getZ(), custom);
            Entity entity = custom.getBaseEntity().orElse(null);
            if (entity != null) applyData(entity);
            return entity;
        } else {
            Entity entity = level.addEntity(loc.getX(), loc.getY(), loc.getZ(), type.left().get());
            applyData(entity);
            return entity;
        }
    }

    /**
     * Applies this saved entity onto an existing entity instance.
     *
     * @param entity the target entity
     */
    public void spawn(Entity entity) {
        spawn(entity, CustomEntitySpawnEvent.SpawnReason.PLUGIN);
    }

    /**
     * Applies this saved entity onto an existing entity instance.
     *
     * @param entity the target entity
     * @param reason the spawn reason
     */
    @SuppressWarnings("unchecked")
    public void spawn(Entity entity, CustomEntitySpawnEvent.SpawnReason reason) {
        if (type.right().isPresent() && entity instanceof LivingEntity living) {
            CustomEntity<LivingEntity> custom = (CustomEntity<LivingEntity>) type.right().get().clone();
            custom.spawn(living, reason);
        }
        applyData(entity);
    }

    /**
     * Applies this saved entity onto a pre-existing native instance.
     *
     * @param nativeEntity the entity instance
     */
    public void spawnFromInstance(Entity nativeEntity) {
        if (type.right().isPresent()) {
            CustomEntity<?> custom = type.right().get().clone();
            custom.spawnFromInstance(nativeEntity);
        }
        applyData(nativeEntity);
    }

    /**
     * Applies serialized data onto a target entity.
     *
     * @param entity the target entity
     */
    @SuppressWarnings("unchecked")
    public void applyData(Entity entity) {
        if (rawData != null && ops != null) {
            DynamicOps<Object> typedOps = (DynamicOps<Object>) this.ops;
            Map<Object, Object> map = typedOps.getMap(this.rawData).orElse(Collections.emptyMap());
            EntityAdapter.load(typedOps, map, entity);
        }
    }
}