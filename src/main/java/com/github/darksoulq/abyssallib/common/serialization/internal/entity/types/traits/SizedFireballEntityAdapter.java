package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SizedFireball;

import java.util.Map;

public class SizedFireballEntityAdapter extends EntityAdapter<SizedFireball> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof SizedFireball;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, SizedFireball value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("display_item", Codecs.ITEM_STACK, value.getDisplayItem());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof SizedFireball fireball)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("display_item", Codecs.ITEM_STACK, opt -> opt.ifPresent(fireball::setDisplayItem));

        return ctx.result();
    }
}