package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
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
    public <D> void serialize(DynamicOps<D> ops, TNTPrimed value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("fuse_ticks"), Codecs.INT.encode(ops, value.getFuseTicks()));

        if (value.getSource() != null) {
            map.put(ops.createString("source_uuid"), Codecs.UUID.encode(ops, value.getSource().getUniqueId()));
        }

        BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getBlockData(), null, null, null);
        map.put(ops.createString("block_info"), ExtraCodecs.BLOCK_INFO.encode(ops, info));
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof TNTPrimed tnt)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("fuse_ticks")))).onSuccess(tnt::setFuseTicks);

        D sourceData = map.get(ops.createString("source_uuid"));
        if (sourceData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, sourceData)).onSuccess(uuid -> {
                Entity source = Bukkit.getEntity(uuid);
                if (source != null) tnt.setSource(source);
            });
        }

        D blockInfoData = map.get(ops.createString("block_info"));
        if (blockInfoData != null) {
            Try.of(() -> ExtraCodecs.BLOCK_INFO.decode(ops, blockInfoData)).onSuccess(info -> {
                if (info.block() instanceof BlockData bd) {
                    tnt.setBlockData(bd);
                }
            });
        }
    }
}