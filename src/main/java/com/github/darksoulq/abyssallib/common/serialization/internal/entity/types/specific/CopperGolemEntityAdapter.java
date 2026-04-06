package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import io.papermc.paper.world.WeatheringCopperState;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.Entity;

import java.util.Map;

public class CopperGolemEntityAdapter extends EntityAdapter<CopperGolem> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof CopperGolem;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, CopperGolem value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("weathering_state"), Codecs.STRING.encode(ops, value.getWeatheringState().name()));
        map.put(ops.createString("golem_state"), Codecs.STRING.encode(ops, value.getGolemState().name()));

        CopperGolem.Oxidizing oxidizing = value.getOxidizing();
        if (oxidizing instanceof CopperGolem.Oxidizing.Waxed) {
            map.put(ops.createString("oxidizing_type"), Codecs.STRING.encode(ops, "waxed"));
        } else if (oxidizing instanceof CopperGolem.Oxidizing.Unset) {
            map.put(ops.createString("oxidizing_type"), Codecs.STRING.encode(ops, "unset"));
        } else if (oxidizing instanceof CopperGolem.Oxidizing.AtTime atTime) {
            map.put(ops.createString("oxidizing_type"), Codecs.STRING.encode(ops, "at_time"));
            map.put(ops.createString("oxidizing_time"), Codecs.LONG.encode(ops, atTime.time()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof CopperGolem golem)) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("weathering_state"))))
                .onSuccess(s -> golem.setWeatheringState(WeatheringCopperState.valueOf(s)));
        
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("golem_state"))))
                .onSuccess(s -> golem.setGolemState(CopperGolem.State.valueOf(s)));

        D oxTypeData = map.get(ops.createString("oxidizing_type"));
        if (oxTypeData != null) {
            Try.of(() -> Codecs.STRING.decode(ops, oxTypeData)).onSuccess(type -> {
                switch (type) {
                    case "waxed":
                        golem.setOxidizing(CopperGolem.Oxidizing.waxed());
                        break;
                    case "unset":
                        golem.setOxidizing(CopperGolem.Oxidizing.unset());
                        break;
                    case "at_time":
                        D oxTimeData = map.get(ops.createString("oxidizing_time"));
                        if (oxTimeData != null) {
                            Try.of(() -> Codecs.LONG.decode(ops, oxTimeData))
                                    .onSuccess(time -> golem.setOxidizing(CopperGolem.Oxidizing.atTime(time)));
                        }
                        break;
                }
            });
        }
    }
}