package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import java.util.Map;

public class VillagerEntityAdapter extends EntityAdapter<Villager> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Villager;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Villager value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("villager_experience"), Codecs.INT.encode(ops, value.getVillagerExperience()));
        map.put(ops.createString("villager_level"), Codecs.INT.encode(ops, value.getVillagerLevel()));
        
        map.put(ops.createString("villager_profession"), Codecs.NAMESPACED_KEY.encode(ops, value.getProfession().getKey()));
        map.put(ops.createString("villager_type"), Codecs.NAMESPACED_KEY.encode(ops, value.getVillagerType().getKey()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Villager villager)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("villager_experience")))).onSuccess(villager::setVillagerExperience);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("villager_level")))).onSuccess(villager::setVillagerLevel);

        D profData = map.get(ops.createString("villager_profession"));
        if (profData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, profData)).onSuccess(key -> {
                Villager.Profession prof = Registry.VILLAGER_PROFESSION.get(key);
                if (prof != null) villager.setProfession(prof);
            });
        }

        D typeData = map.get(ops.createString("villager_type"));
        if (typeData != null) {
            Try.of(() -> Codecs.NAMESPACED_KEY.decode(ops, typeData)).onSuccess(key -> {
                Villager.Type type = Registry.VILLAGER_TYPE.get(key);
                if (type != null) villager.setVillagerType(type);
            });
        }
    }
}