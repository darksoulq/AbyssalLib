package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ThrownPotion;

import java.util.Map;

public class ThrownPotionEntityAdapter extends EntityAdapter<ThrownPotion> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ThrownPotion;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ThrownPotion value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("item", Codecs.ITEM_STACK, value.getItem());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ThrownPotion potion)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("item", Codecs.ITEM_STACK, opt -> opt.ifPresent(potion::setItem));

        return ctx.result();
    }
}