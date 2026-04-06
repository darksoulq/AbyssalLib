package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BeeEntityAdapter extends EntityAdapter<Bee> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Bee;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Bee value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("has_nectar"), Codecs.BOOLEAN.encode(ops, value.hasNectar()));
        map.put(ops.createString("has_stung"), Codecs.BOOLEAN.encode(ops, value.hasStung()));
        map.put(ops.createString("anger"), Codecs.INT.encode(ops, value.getAnger()));
        map.put(ops.createString("cannot_enter_hive_ticks"), Codecs.INT.encode(ops, value.getCannotEnterHiveTicks()));

        if (value.getFlower() != null) map.put(ops.createString("flower_pos"), Codecs.LOCATION.encode(ops, value.getFlower()));
        if (value.getHive() != null) map.put(ops.createString("hive_pos"), Codecs.LOCATION.encode(ops, value.getHive()));

        map.put(ops.createString("rolling_override"), Codecs.STRING.encode(ops, value.getRollingOverride().name()));
        map.put(ops.createString("crops_grown_since_pollination"), Codecs.INT.encode(ops, value.getCropsGrownSincePollination()));
        map.put(ops.createString("ticks_since_pollination"), Codecs.INT.encode(ops, value.getTicksSincePollination()));
        map.put(ops.createString("time_since_sting"), Codecs.INT.encode(ops, value.getTimeSinceSting()));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Bee bee)) return;

        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_nectar")))).onSuccess(bee::setHasNectar);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("has_stung")))).onSuccess(bee::setHasStung);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("anger")))).onSuccess(bee::setAnger);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("cannot_enter_hive_ticks")))).onSuccess(bee::setCannotEnterHiveTicks);

        D flower = map.get(ops.createString("flower_pos"));
        if (flower != null) Try.of(() -> Codecs.LOCATION.decode(ops, flower)).onSuccess(bee::setFlower);

        D hive = map.get(ops.createString("hive_pos"));
        if (hive != null) Try.of(() -> Codecs.LOCATION.decode(ops, hive)).onSuccess(bee::setHive);

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("rolling_override")))).onSuccess(s -> bee.setRollingOverride(TriState.valueOf(s)));
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("crops_grown_since_pollination")))).onSuccess(bee::setCropsGrownSincePollination);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("ticks_since_pollination")))).onSuccess(bee::setTicksSincePollination);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("time_since_sting")))).onSuccess(bee::setTimeSinceSting);
    }
}