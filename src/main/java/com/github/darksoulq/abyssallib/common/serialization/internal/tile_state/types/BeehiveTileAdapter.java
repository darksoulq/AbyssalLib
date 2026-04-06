package com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import com.github.darksoulq.abyssallib.world.entity.SavedEntity;
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
    public <D> D serialize(DynamicOps<D> ops, Beehive value) throws Codec.CodecException {
        Map<D, D> map = new HashMap<>();

        map.put(ops.createString("max_entities"), Codecs.INT.encode(ops, value.getMaxEntities()));

        if (value.getFlower() != null) {
            map.put(ops.createString("flower"), Codecs.LOCATION.encode(ops, value.getFlower()));
        }

        Try.run(() -> {
            CraftBlockEntityState<?> tile = (CraftBlockEntityState<?>) value;
            Method getSnapshotMethod = CraftBlockEntityState.class.getDeclaredMethod("getSnapshot");
            getSnapshotMethod.setAccessible(true);
            BeehiveBlockEntity beehive = (BeehiveBlockEntity) getSnapshotMethod.invoke(tile);

            Method getBeesMethod = BeehiveBlockEntity.class.getDeclaredMethod("getBees");
            getBeesMethod.setAccessible(true);
            List<BeehiveBlockEntity.Occupant> nmsBees = (List<BeehiveBlockEntity.Occupant>) getBeesMethod.invoke(beehive);

            net.minecraft.world.level.Level level = beehive.getLevel();
            if (level == null) {
                org.bukkit.World bukkitWorld = value.isPlaced() ? value.getWorld() : org.bukkit.Bukkit.getWorlds().get(0);
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
                map.put(ops.createString("bees"), ExtraCodecs.SAVED_ENTITY.list().encode(ops, savedEntities));
            }
        });

        return ops.createMap(map);
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, D input, TileState base) throws Codec.CodecException {
        if (!(base instanceof Beehive hive)) return;
        Map<D, D> map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map for Beehive"));

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("max_entities")))).onSuccess(hive::setMaxEntities);

        D flowerData = map.get(ops.createString("flower"));
        if (flowerData != null) {
            Try.of(() -> Codecs.LOCATION.decode(ops, flowerData)).onSuccess(hive::setFlower);
        }

        Try.run(() -> {
            D beesData = map.get(ops.createString("bees"));
            if (beesData != null) {
                CraftBlockEntityState<?> tile = (CraftBlockEntityState<?>) hive;
                Method getSnapshotMethod = CraftBlockEntityState.class.getDeclaredMethod("getSnapshot");
                getSnapshotMethod.setAccessible(true);
                BeehiveBlockEntity beehive = (BeehiveBlockEntity) getSnapshotMethod.invoke(tile);

                List<SavedEntity> occupants = ExtraCodecs.SAVED_ENTITY.list().decode(ops, beesData);
                beehive.clearBees();

                Location spawnLoc = hive.isPlaced() ? hive.getLocation() : new Location(org.bukkit.Bukkit.getWorlds().get(0), 0, 0, 0);

                for (SavedEntity saved : occupants) {
                    org.bukkit.entity.Entity bukkitEntity = saved.spawn(spawnLoc);
                    if (bukkitEntity instanceof Bee bee) {
                        beehive.addOccupant(((CraftBee) bee).getHandle());
                    }
                    bukkitEntity.remove();
                }
            }
        });
    }
}