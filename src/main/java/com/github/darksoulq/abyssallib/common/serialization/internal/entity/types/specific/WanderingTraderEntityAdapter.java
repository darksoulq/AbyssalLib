package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WanderingTrader;

import java.util.Map;

public class WanderingTraderEntityAdapter extends EntityAdapter<WanderingTrader> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof WanderingTrader;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, WanderingTrader value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("despawn_delay", Codecs.INT, value.getDespawnDelay())
            .write("can_drink_potion", Codecs.BOOLEAN, value.canDrinkPotion())
            .write("can_drink_milk", Codecs.BOOLEAN, value.canDrinkMilk())
            .writeNullable("wandering_towards", Codecs.LOCATION, value.getWanderingTowards());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof WanderingTrader trader)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("despawn_delay", Codecs.INT, opt -> opt.ifPresent(trader::setDespawnDelay))
            .readOptional("can_drink_potion", Codecs.BOOLEAN, opt -> opt.ifPresent(trader::setCanDrinkPotion))
            .readOptional("can_drink_milk", Codecs.BOOLEAN, opt -> opt.ifPresent(trader::setCanDrinkMilk))
            .readOptional("wandering_towards", Codecs.LOCATION, opt -> opt.ifPresent(trader::setWanderingTowards));

        return ctx.result();
    }
}