package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.bukkit.Location;
import org.bukkit.block.Beehive;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.entity.CraftBee;
import org.bukkit.entity.Bee;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class BeehiveTileAdapter extends TileAdapter<Beehive> {

    @Override
    public boolean doesApply(TileState state) {
        return state instanceof Beehive;
    }

    @Override
    public <D> DataResult<D> serialize(DynamicOps<D> ops, Beehive value) {
        Map<D, D> map = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        DataResult<D> maxEntRes = Codecs.INT.encode(ops, value.getMaxEntities()).prependPath("max_entities");
        if (maxEntRes.isError()) {
            warnings.add(maxEntRes.dataError().orElseGet(() -> DataError.custom(maxEntRes.error().get())));
        } else {
            map.put(ops.createString("max_entities"), maxEntRes.getOrThrow());
            if (maxEntRes.isPartial()) warnings.addAll(maxEntRes.warnings());
        }

        if (value.getFlower() != null) {
            DataResult<D> flowerRes = Codecs.LOCATION.encode(ops, value.getFlower()).prependPath("flower");
            if (flowerRes.isError()) {
                warnings.add(flowerRes.dataError().orElseGet(() -> DataError.custom(flowerRes.error().get())));
            } else {
                map.put(ops.createString("flower"), flowerRes.getOrThrow());
                if (flowerRes.isPartial()) warnings.addAll(flowerRes.warnings());
            }
        }

        try {
            CraftBlockEntityState<?> tile = (CraftBlockEntityState<?>) value;
            Method getSnapshotMethod = CraftBlockEntityState.class.getDeclaredMethod("getSnapshot");
            getSnapshotMethod.setAccessible(true);
            BeehiveBlockEntity beehive = (BeehiveBlockEntity) getSnapshotMethod.invoke(tile);

            Method getBeesMethod = BeehiveBlockEntity.class.getDeclaredMethod("getBees");
            getBeesMethod.setAccessible(true);
            List<BeehiveBlockEntity.Occupant> nmsBees = (List<BeehiveBlockEntity.Occupant>) getBeesMethod.invoke(beehive);

            net.minecraft.world.level.Level level = beehive.getLevel();
            if (level == null) {
                org.bukkit.World bukkitWorld = value.isPlaced() ? value.getWorld() : org.bukkit.Bukkit.getWorlds().getFirst();
                level = ((org.bukkit.craftbukkit.CraftWorld) bukkitWorld).getHandle();
            }

            List<SavedEntity> savedEntities = new ArrayList<>();
            for (BeehiveBlockEntity.Occupant occupant : nmsBees) {
                Entity entity = occupant.createEntity(level, beehive.getBlockPos());
                if (entity != null) {
                    savedEntities.add(SavedEntity.create(entity.getBukkitEntity(), ops));
                    entity.discard();
                }
            }

            if (!savedEntities.isEmpty()) {
                DataResult<D> beesRes = ExtraCodecs.SAVED_ENTITY.list().encode(ops, savedEntities).prependPath("bees");
                if (beesRes.isError()) {
                    warnings.add(beesRes.dataError().orElseGet(() -> DataError.custom(beesRes.error().get())));
                } else {
                    map.put(ops.createString("bees"), beesRes.getOrThrow());
                    if (beesRes.isPartial()) warnings.addAll(beesRes.warnings());
                }
            }
        } catch (Exception e) {
            warnings.add(DataError.custom("Reflection logic failed while retrieving bees: " + e.getMessage()));
        }

        return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, TileState base) {
        if (!(base instanceof Beehive hive)) return DataResult.success(null);

        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
            .flatMap(map -> {
                List<DataError> warnings = new ArrayList<>();

                D maxEntitiesData = map.get(ops.createString("max_entities"));
                if (maxEntitiesData != null) {
                    DataResult<Integer> res = Codecs.INT.decode(ops, maxEntitiesData).prependPath("max_entities");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        hive.setMaxEntities(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D flowerData = map.get(ops.createString("flower"));
                if (flowerData != null) {
                    DataResult<Location> res = Codecs.LOCATION.decode(ops, flowerData).prependPath("flower");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        hive.setFlower(res.getOrThrow());
                        if (res.isPartial()) warnings.addAll(res.warnings());
                    }
                }

                D beesData = map.get(ops.createString("bees"));
                if (beesData != null) {
                    DataResult<List<SavedEntity>> res = ExtraCodecs.SAVED_ENTITY.list().decode(ops, beesData).prependPath("bees");
                    if (res.isError()) {
                        warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                    } else {
                        if (res.isPartial()) warnings.addAll(res.warnings());
                        try {
                            CraftBlockEntityState<?> tile = (CraftBlockEntityState<?>) hive;
                            Method getSnapshotMethod = CraftBlockEntityState.class.getDeclaredMethod("getSnapshot");
                            getSnapshotMethod.setAccessible(true);
                            BeehiveBlockEntity beehive = (BeehiveBlockEntity) getSnapshotMethod.invoke(tile);

                            List<SavedEntity> occupants = res.getOrThrow();
                            beehive.clearBees();

                            Location spawnLoc = hive.isPlaced() ? hive.getLocation() : new Location(org.bukkit.Bukkit.getWorlds().getFirst(), 0, 0, 0);

                            for (SavedEntity saved : occupants) {
                                org.bukkit.entity.Entity bukkitEntity = saved.spawn(spawnLoc);
                                if (bukkitEntity instanceof Bee bee) {
                                    beehive.addOccupant(((CraftBee) bee).getHandle());
                                }
                                if (bukkitEntity != null) bukkitEntity.remove();
                            }
                        } catch (Exception e) {
                            warnings.add(DataError.custom("Reflection logic failed while modifying bees: " + e.getMessage()));
                        }
                    }
                }

                return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
            });
    }
}