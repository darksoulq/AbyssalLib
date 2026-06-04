package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;

import java.util.Map;

public class ParrotEntityAdapter extends EntityAdapter<Parrot> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Parrot;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Parrot value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);
        ctx.write("parrot_variant", Codecs.STRING, value.getVariant().name());
        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Parrot parrot)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("parrot_variant", Codecs.STRING, opt -> opt.ifPresent(variantStr -> {
            try {
                parrot.setVariant(Parrot.Variant.valueOf(variantStr));
            } catch (Exception ignored) {
            }
        }));

        return ctx.result();
    }
}