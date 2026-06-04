package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sniffer;

import java.util.ArrayList;
import java.util.Map;

public class SnifferEntityAdapter extends EntityAdapter<Sniffer> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Sniffer;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Sniffer value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("state", Codecs.STRING, value.getState().name());

        if (!value.getExploredLocations().isEmpty()) {
            ctx.write("explored_locations", Codecs.LOCATION.list(), new ArrayList<>(value.getExploredLocations()));
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Sniffer sniffer)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("state", Codecs.STRING, opt -> opt.ifPresent(stateStr -> {
                try {
                    sniffer.setState(Sniffer.State.valueOf(stateStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("explored_locations", Codecs.LOCATION.list(), opt -> opt.ifPresent(locs -> {
                locs.forEach(sniffer::addExploredLocation);
            }));

        return ctx.result();
    }
}