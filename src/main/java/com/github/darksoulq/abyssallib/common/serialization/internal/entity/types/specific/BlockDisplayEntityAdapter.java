package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Map;

public class BlockDisplayEntityAdapter extends EntityAdapter<BlockDisplay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof BlockDisplay;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, BlockDisplay value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getBlock(), null, null, null);
        ctx.write("block_info", ExtraCodecs.BLOCK_INFO, info);
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof BlockDisplay display)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("block_info", ExtraCodecs.BLOCK_INFO, opt -> opt.ifPresent(info -> {
            if (info.block() instanceof BlockData bd) {
                display.setBlock(bd);
            }
        }));

        return ctx.result();
    }
}