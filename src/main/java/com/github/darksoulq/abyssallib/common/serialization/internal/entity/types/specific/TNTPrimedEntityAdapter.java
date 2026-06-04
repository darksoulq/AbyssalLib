package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

import java.util.Map;

public class TNTPrimedEntityAdapter extends EntityAdapter<TNTPrimed> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof TNTPrimed;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, TNTPrimed value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("fuse_ticks", Codecs.INT, value.getFuseTicks());

        if (value.getSource() != null) {
            ctx.write("source_uuid", Codecs.UUID, value.getSource().getUniqueId());
        }

        BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getBlockData(), null, null, null);
        ctx.write("block_info", ExtraCodecs.BLOCK_INFO, info);

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof TNTPrimed tnt)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("fuse_ticks", Codecs.INT, opt -> opt.ifPresent(tnt::setFuseTicks))
            .readOptional("source_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Entity source = Bukkit.getEntity(uuid);
                if (source != null) tnt.setSource(source);
            }))
            .readOptional("block_info", ExtraCodecs.BLOCK_INFO, opt -> opt.ifPresent(info -> {
                if (info.block() instanceof BlockData bd) {
                    tnt.setBlockData(bd);
                }
            }));

        return ctx.result();
    }
}