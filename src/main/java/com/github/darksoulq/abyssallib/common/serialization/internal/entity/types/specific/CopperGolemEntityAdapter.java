package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, CopperGolem value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("weathering_state", Codecs.STRING, value.getWeatheringState().name())
            .write("golem_state", Codecs.STRING, value.getGolemState().name());

        CopperGolem.Oxidizing oxidizing = value.getOxidizing();
        if (oxidizing instanceof CopperGolem.Oxidizing.Waxed) {
            ctx.write("oxidizing_type", Codecs.STRING, "waxed");
        } else if (oxidizing instanceof CopperGolem.Oxidizing.Unset) {
            ctx.write("oxidizing_type", Codecs.STRING, "unset");
        } else if (oxidizing instanceof CopperGolem.Oxidizing.AtTime atTime) {
            ctx.write("oxidizing_type", Codecs.STRING, "at_time")
                .write("oxidizing_time", Codecs.LONG, atTime.time());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof CopperGolem golem)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("weathering_state", Codecs.STRING, opt -> opt.ifPresent(state -> {
                try {
                    golem.setWeatheringState(WeatheringCopperState.valueOf(state));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("golem_state", Codecs.STRING, opt -> opt.ifPresent(state -> {
                try {
                    golem.setGolemState(CopperGolem.State.valueOf(state));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("oxidizing_type", Codecs.STRING, opt -> opt.ifPresent(type -> {
                switch (type) {
                    case "waxed":
                        golem.setOxidizing(CopperGolem.Oxidizing.waxed());
                        break;
                    case "unset":
                        golem.setOxidizing(CopperGolem.Oxidizing.unset());
                        break;
                    case "at_time":
                        ctx.readOptional("oxidizing_time", Codecs.LONG, timeOpt -> timeOpt.ifPresent(time -> {
                            golem.setOxidizing(CopperGolem.Oxidizing.atTime(time));
                        }));
                        break;
                }
            }));

        return ctx.result();
    }
}