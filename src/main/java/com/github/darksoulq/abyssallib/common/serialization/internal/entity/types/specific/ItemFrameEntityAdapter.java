package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Map;

public class ItemFrameEntityAdapter extends EntityAdapter<ItemFrame> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof ItemFrame;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, ItemFrame value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("rotation", Codecs.STRING, value.getRotation().name())
            .write("is_visible", Codecs.BOOLEAN, value.isVisible())
            .write("is_fixed", Codecs.BOOLEAN, value.isFixed())
            .write("item_drop_chance", Codecs.FLOAT, value.getItemDropChance());

        if (!value.getItem().isEmpty()) {
            ctx.write("item", Codecs.ITEM_STACK, value.getItem());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof ItemFrame frame)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("rotation", Codecs.STRING, opt -> opt.ifPresent(rot -> {
                try {
                    frame.setRotation(Rotation.valueOf(rot));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("is_visible", Codecs.BOOLEAN, opt -> opt.ifPresent(frame::setVisible))
            .readOptional("is_fixed", Codecs.BOOLEAN, opt -> opt.ifPresent(frame::setFixed))
            .readOptional("item_drop_chance", Codecs.FLOAT, opt -> opt.ifPresent(frame::setItemDropChance))
            .readOptional("item", Codecs.ITEM_STACK, opt -> opt.ifPresent(frame::setItem));

        return ctx.result();
    }
}