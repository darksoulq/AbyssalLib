package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;

import java.util.Map;

public class InteractionEntityAdapter extends EntityAdapter<Interaction> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Interaction;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Interaction value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("interaction_width", Codecs.FLOAT, value.getInteractionWidth())
            .write("interaction_height", Codecs.FLOAT, value.getInteractionHeight())
            .write("responsive", Codecs.BOOLEAN, value.isResponsive());

        if (value.getLastAttack() != null) {
            EncodeContext<D> attackCtx = EncodeContext.of(ops);
            attackCtx.write("player_uuid", Codecs.UUID, value.getLastAttack().getPlayer().getUniqueId());
            attackCtx.write("timestamp", Codecs.LONG, value.getLastAttack().getTimestamp());
            DataResult<D> atkRes = attackCtx.result();
            if (atkRes.isSuccess()) {
                map.put(ops.createString("last_attack"), atkRes.getOrThrow());
            }
        }

        if (value.getLastInteraction() != null) {
            EncodeContext<D> intCtx = EncodeContext.of(ops);
            intCtx.write("player_uuid", Codecs.UUID, value.getLastInteraction().getPlayer().getUniqueId());
            intCtx.write("timestamp", Codecs.LONG, value.getLastInteraction().getTimestamp());
            DataResult<D> intRes = intCtx.result();
            if (intRes.isSuccess()) {
                map.put(ops.createString("last_interaction"), intRes.getOrThrow());
            }
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Interaction interaction)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("interaction_width", Codecs.FLOAT, opt -> opt.ifPresent(interaction::setInteractionWidth))
            .readOptional("interaction_height", Codecs.FLOAT, opt -> opt.ifPresent(interaction::setInteractionHeight))
            .readOptional("responsive", Codecs.BOOLEAN, opt -> opt.ifPresent(interaction::setResponsive));

        return ctx.result();
    }
}