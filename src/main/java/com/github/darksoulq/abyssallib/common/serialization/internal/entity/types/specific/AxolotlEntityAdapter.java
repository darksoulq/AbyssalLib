package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;

import java.util.Map;

public class AxolotlEntityAdapter extends EntityAdapter<Axolotl> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Axolotl;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Axolotl value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("axolotl_variant", Codecs.STRING, value.getVariant().name())
            .write("is_playing_dead", Codecs.BOOLEAN, value.isPlayingDead());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Axolotl axolotl)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("axolotl_variant", Codecs.STRING, opt -> opt.ifPresent(variantStr -> {
                try {
                    axolotl.setVariant(Axolotl.Variant.valueOf(variantStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("is_playing_dead", Codecs.BOOLEAN, opt -> opt.ifPresent(axolotl::setPlayingDead));

        return ctx.result();
    }
}