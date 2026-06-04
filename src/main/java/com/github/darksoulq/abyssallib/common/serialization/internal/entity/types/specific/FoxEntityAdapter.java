package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;

import java.util.Map;

public class FoxEntityAdapter extends EntityAdapter<Fox> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Fox;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Fox value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("fox_type", Codecs.STRING, value.getFoxType().name())
            .write("is_crouching", Codecs.BOOLEAN, value.isCrouching())
            .write("is_sleeping", Codecs.BOOLEAN, value.isSleeping())
            .write("is_faceplanted", Codecs.BOOLEAN, value.isFaceplanted())
            .write("is_interested", Codecs.BOOLEAN, value.isInterested())
            .write("is_leaping", Codecs.BOOLEAN, value.isLeaping())
            .write("is_defending", Codecs.BOOLEAN, value.isDefending());

        if (value.getFirstTrustedPlayer() != null) {
            ctx.write("first_trusted_uuid", Codecs.UUID, value.getFirstTrustedPlayer().getUniqueId());
        }

        if (value.getSecondTrustedPlayer() != null) {
            ctx.write("second_trusted_uuid", Codecs.UUID, value.getSecondTrustedPlayer().getUniqueId());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Fox fox)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("fox_type", Codecs.STRING, opt -> opt.ifPresent(typeStr -> {
                try {
                    fox.setFoxType(Fox.Type.valueOf(typeStr));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("is_crouching", Codecs.BOOLEAN, opt -> opt.ifPresent(fox::setCrouching))
            .readOptional("is_sleeping", Codecs.BOOLEAN, opt -> opt.ifPresent(fox::setSleeping))
            .readOptional("is_faceplanted", Codecs.BOOLEAN, opt -> opt.ifPresent(fox::setFaceplanted))
            .readOptional("is_interested", Codecs.BOOLEAN, opt -> opt.ifPresent(fox::setInterested))
            .readOptional("is_leaping", Codecs.BOOLEAN, opt -> opt.ifPresent(fox::setLeaping))
            .readOptional("is_defending", Codecs.BOOLEAN, opt -> opt.ifPresent(fox::setDefending))
            .readOptional("first_trusted_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> fox.setFirstTrustedPlayer(Bukkit.getOfflinePlayer(uuid))))
            .readOptional("second_trusted_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> fox.setSecondTrustedPlayer(Bukkit.getOfflinePlayer(uuid))));

        return ctx.result();
    }
}