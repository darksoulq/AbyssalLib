package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.MainHand;

import java.util.Map;

public class MannequinEntityAdapter extends EntityAdapter<Mannequin> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Mannequin;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Mannequin value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("is_immovable", Codecs.BOOLEAN, value.isImmovable())
            .write("main_hand", Codecs.STRING, value.getMainHand().name())
            .write("profile", ExtraCodecs.RESOLVABLE_PROFILE, value.getProfile())
            .writeNullable("description", Codecs.TEXT_COMPONENT, value.getDescription());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Mannequin mannequin)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("is_immovable", Codecs.BOOLEAN, opt -> opt.ifPresent(mannequin::setImmovable))
            .readOptional("main_hand", Codecs.STRING, opt -> opt.ifPresent(hand -> {
                try {
                    mannequin.setMainHand(MainHand.valueOf(hand));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("profile", ExtraCodecs.RESOLVABLE_PROFILE, opt -> opt.ifPresent(mannequin::setProfile))
            .readOptional("description", Codecs.TEXT_COMPONENT, opt -> opt.ifPresent(mannequin::setDescription));

        return ctx.result();
    }
}