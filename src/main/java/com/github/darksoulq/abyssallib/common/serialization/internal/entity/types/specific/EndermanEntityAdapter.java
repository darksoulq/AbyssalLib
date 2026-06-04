package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Map;

public class EndermanEntityAdapter extends EntityAdapter<Enderman> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Enderman;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Enderman value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_screaming", Codecs.BOOLEAN, value.isScreaming())
            .write("has_been_stared_at", Codecs.BOOLEAN, value.hasBeenStaredAt());

        if (value.getCarriedBlock() != null) {
            BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getCarriedBlock(), null, null, null);
            ctx.write("carried_block", ExtraCodecs.BLOCK_INFO, info);
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Enderman enderman)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_screaming", Codecs.BOOLEAN, opt -> opt.ifPresent(enderman::setScreaming))
            .readOptional("has_been_stared_at", Codecs.BOOLEAN, opt -> opt.ifPresent(enderman::setHasBeenStaredAt))
            .readOptional("carried_block", ExtraCodecs.BLOCK_INFO, opt -> opt.ifPresent(info -> {
                if (info.block() instanceof BlockData data) {
                    enderman.setCarriedBlock(data);
                }
            }));

        return ctx.result();
    }
}