package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

import java.util.Map;

public class ItemDisplayEntityAdapter extends EntityAdapter<ItemDisplay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ItemDisplay;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ItemDisplay value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("item_display_transform", Codecs.ITEM_DISPLAY_TRANSFORM, value.getItemDisplayTransform());

        if (!value.getItemStack().isEmpty()) {
            ctx.write("item_stack", Codecs.ITEM_STACK, value.getItemStack());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ItemDisplay display)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("item_display_transform", Codecs.ITEM_DISPLAY_TRANSFORM, opt -> opt.ifPresent(display::setItemDisplayTransform))
            .readOptional("item_stack", Codecs.ITEM_STACK, opt -> opt.ifPresent(display::setItemStack));

        return ctx.result();
    }
}