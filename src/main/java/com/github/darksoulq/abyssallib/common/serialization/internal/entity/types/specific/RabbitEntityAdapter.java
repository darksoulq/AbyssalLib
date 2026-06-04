package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;

import java.util.Map;

public class RabbitEntityAdapter extends EntityAdapter<Rabbit> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Rabbit;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Rabbit value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("rabbit_type", Codecs.STRING, value.getRabbitType().name())
            .write("more_carrot_ticks", Codecs.INT, value.getMoreCarrotTicks());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Rabbit rabbit)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("rabbit_type", Codecs.STRING, opt -> opt.ifPresent(typeStr -> {
                try {
                    rabbit.setRabbitType(Rabbit.Type.valueOf(typeStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("more_carrot_ticks", Codecs.INT, opt -> opt.ifPresent(rabbit::setMoreCarrotTicks));

        return ctx.result();
    }
}