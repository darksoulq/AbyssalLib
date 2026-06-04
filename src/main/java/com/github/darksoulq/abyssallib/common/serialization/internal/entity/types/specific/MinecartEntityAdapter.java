package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.util.Vector;

import java.util.Map;

public class MinecartEntityAdapter extends EntityAdapter<Minecart> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Minecart;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Minecart value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("damage", Codecs.DOUBLE, value.getDamage())
            .write("max_speed", Codecs.DOUBLE, value.getMaxSpeed())
            .write("is_slow_when_empty", Codecs.BOOLEAN, value.isSlowWhenEmpty())
            .write("display_block_offset", Codecs.INT, value.getDisplayBlockOffset())
            .write("flying_velocity_mod", Codecs.VECTOR_F, value.getFlyingVelocityMod())
            .write("derailed_velocity_mod", Codecs.VECTOR_F, value.getDerailedVelocityMod());

        BlockInfo info = new BlockInfo(new Vector(0, 0, 0), value.getDisplayBlockData(), null, null, null);
        ctx.write("display_block_info", ExtraCodecs.BLOCK_INFO, info);

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Minecart minecart)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("damage", Codecs.DOUBLE, opt -> opt.ifPresent(minecart::setDamage))
            .readOptional("max_speed", Codecs.DOUBLE, opt -> opt.ifPresent(minecart::setMaxSpeed))
            .readOptional("is_slow_when_empty", Codecs.BOOLEAN, opt -> opt.ifPresent(minecart::setSlowWhenEmpty))
            .readOptional("display_block_offset", Codecs.INT, opt -> opt.ifPresent(minecart::setDisplayBlockOffset))
            .readOptional("flying_velocity_mod", Codecs.VECTOR_F, opt -> opt.ifPresent(minecart::setFlyingVelocityMod))
            .readOptional("derailed_velocity_mod", Codecs.VECTOR_F, opt -> opt.ifPresent(minecart::setDerailedVelocityMod))
            .readOptional("display_block_info", ExtraCodecs.BLOCK_INFO, opt -> opt.ifPresent(info -> {
                if (info.block() instanceof BlockData bd) {
                    minecart.setDisplayBlockData(bd);
                }
            }));

        return ctx.result();
    }
}