package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;

import java.util.Map;

public class ShulkerEntityAdapter extends EntityAdapter<Shulker> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Shulker;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Shulker value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("peek", Codecs.FLOAT, value.getPeek())
            .write("attached_face", Codecs.STRING, value.getAttachedFace().name());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Shulker shulker)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("peek", Codecs.FLOAT, opt -> opt.ifPresent(shulker::setPeek))
            .readOptional("attached_face", Codecs.STRING, opt -> opt.ifPresent(faceStr -> {
                try {
                    shulker.setAttachedFace(BlockFace.valueOf(faceStr));
                } catch (Exception ignored) {
                }
            }));

        return ctx.result();
    }
}