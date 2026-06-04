package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Bee value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("has_nectar", Codecs.BOOLEAN, value.hasNectar())
            .write("has_stung", Codecs.BOOLEAN, value.hasStung())
            .write("anger", Codecs.INT, value.getAnger())
            .write("cannot_enter_hive_ticks", Codecs.INT, value.getCannotEnterHiveTicks())
            .writeNullable("flower_pos", Codecs.LOCATION, value.getFlower())
            .writeNullable("hive_pos", Codecs.LOCATION, value.getHive())
            .write("rolling_override", Codecs.STRING, value.getRollingOverride().name())
            .write("crops_grown_since_pollination", Codecs.INT, value.getCropsGrownSincePollination())
            .write("ticks_since_pollination", Codecs.INT, value.getTicksSincePollination())
            .write("time_since_sting", Codecs.INT, value.getTimeSinceSting());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Bee bee)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("has_nectar", Codecs.BOOLEAN, opt -> opt.ifPresent(bee::setHasNectar))
            .readOptional("has_stung", Codecs.BOOLEAN, opt -> opt.ifPresent(bee::setHasStung))
            .readOptional("anger", Codecs.INT, opt -> opt.ifPresent(bee::setAnger))
            .readOptional("cannot_enter_hive_ticks", Codecs.INT, opt -> opt.ifPresent(bee::setCannotEnterHiveTicks))
            .readOptional("flower_pos", Codecs.LOCATION, opt -> opt.ifPresent(bee::setFlower))
            .readOptional("hive_pos", Codecs.LOCATION, opt -> opt.ifPresent(bee::setHive))
            .readOptional("rolling_override", Codecs.STRING, opt -> opt.ifPresent(state -> {
                try {
                    bee.setRollingOverride(TriState.valueOf(state));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("crops_grown_since_pollination", Codecs.INT, opt -> opt.ifPresent(bee::setCropsGrownSincePollination))
            .readOptional("ticks_since_pollination", Codecs.INT, opt -> opt.ifPresent(bee::setTicksSincePollination))
            .readOptional("time_since_sting", Codecs.INT, opt -> opt.ifPresent(bee::setTimeSinceSting));

        return ctx.result();
    }
}