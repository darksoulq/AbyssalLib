package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

import java.util.Map;

public class ZombieEntityAdapter extends EntityAdapter<Zombie> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Zombie;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Zombie value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("can_break_doors"), Codecs.BOOLEAN.encode(ops, value.canBreakDoors()));
        map.put(ops.createString("should_burn_in_day"), Codecs.BOOLEAN.encode(ops, value.shouldBurnInDay()));
        if (value.isConverting()) {
            map.put(ops.createString("conversion_time"), Codecs.INT.encode(ops, value.getConversionTime()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Zombie zombie)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("can_break_doors")))).onSuccess(zombie::setCanBreakDoors);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("should_burn_in_day")))).onSuccess(zombie::setShouldBurnInDay);
        D conversionData = map.get(ops.createString("conversion_time"));
        if (conversionData != null) {
            Try.of(() -> Codecs.INT.decode(ops, conversionData)).onSuccess(zombie::setConversionTime);
        }
    }
}