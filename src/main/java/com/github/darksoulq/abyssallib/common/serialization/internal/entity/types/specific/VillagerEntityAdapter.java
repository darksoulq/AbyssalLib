package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Villager value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("villager_experience", Codecs.INT, value.getVillagerExperience())
            .write("villager_level", Codecs.INT, value.getVillagerLevel())
            .write("villager_profession", Codecs.NAMESPACED_KEY, value.getProfession().getKey())
            .write("villager_type", Codecs.NAMESPACED_KEY, value.getVillagerType().getKey());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Villager villager)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("villager_experience", Codecs.INT, opt -> opt.ifPresent(villager::setVillagerExperience))
            .readOptional("villager_level", Codecs.INT, opt -> opt.ifPresent(villager::setVillagerLevel))
            .readOptional("villager_profession", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                Villager.Profession prof = Registry.VILLAGER_PROFESSION.get(key);
                if (prof != null) villager.setProfession(prof);
            }))
            .readOptional("villager_type", Codecs.NAMESPACED_KEY, opt -> opt.ifPresent(key -> {
                Villager.Type type = Registry.VILLAGER_TYPE.get(key);
                if (type != null) villager.setVillagerType(type);
            }));

        return ctx.result();
    }
}