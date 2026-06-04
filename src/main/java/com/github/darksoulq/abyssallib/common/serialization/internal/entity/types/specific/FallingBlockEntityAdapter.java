package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;

import java.util.Map;

public class FallingBlockEntityAdapter extends EntityAdapter<FallingBlock> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof FallingBlock;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, FallingBlock value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("block_data", Codecs.STRING, value.getBlockData().getAsString())
            .write("drop_item", Codecs.BOOLEAN, value.getDropItem())
            .write("cancel_drop", Codecs.BOOLEAN, value.getCancelDrop())
            .write("hurt_entities", Codecs.BOOLEAN, value.canHurtEntities())
            .write("damage_per_block", Codecs.FLOAT, value.getDamagePerBlock())
            .write("max_damage", Codecs.INT, value.getMaxDamage())
            .write("auto_expire", Codecs.BOOLEAN, value.doesAutoExpire());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof FallingBlock block)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("block_data", Codecs.STRING, opt -> opt.ifPresent(dataStr -> {
                try {
                    block.setBlockData(Bukkit.createBlockData(dataStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("drop_item", Codecs.BOOLEAN, opt -> opt.ifPresent(block::setDropItem))
            .readOptional("cancel_drop", Codecs.BOOLEAN, opt -> opt.ifPresent(block::setCancelDrop))
            .readOptional("hurt_entities", Codecs.BOOLEAN, opt -> opt.ifPresent(block::setHurtEntities))
            .readOptional("damage_per_block", Codecs.FLOAT, opt -> opt.ifPresent(block::setDamagePerBlock))
            .readOptional("max_damage", Codecs.INT, opt -> opt.ifPresent(block::setMaxDamage))
            .readOptional("auto_expire", Codecs.BOOLEAN, opt -> opt.ifPresent(block::shouldAutoExpire));

        return ctx.result();
    }
}