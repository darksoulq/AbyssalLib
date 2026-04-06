package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;

import java.util.HashMap;
import java.util.Map;

public class InteractionEntityAdapter extends EntityAdapter<Interaction> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Interaction;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Interaction value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("interaction_width"), Codecs.FLOAT.encode(ops, value.getInteractionWidth()));
        map.put(ops.createString("interaction_height"), Codecs.FLOAT.encode(ops, value.getInteractionHeight()));
        map.put(ops.createString("responsive"), Codecs.BOOLEAN.encode(ops, value.isResponsive()));

        if (value.getLastAttack() != null) {
            Map<D, D> attackMap = new HashMap<>();
            attackMap.put(ops.createString("player_uuid"), Codecs.UUID.encode(ops, value.getLastAttack().getPlayer().getUniqueId()));
            attackMap.put(ops.createString("timestamp"), Codecs.LONG.encode(ops, value.getLastAttack().getTimestamp()));
            map.put(ops.createString("last_attack"), ops.createMap(attackMap));
        }

        if (value.getLastInteraction() != null) {
            Map<D, D> interactionMap = new HashMap<>();
            interactionMap.put(ops.createString("player_uuid"), Codecs.UUID.encode(ops, value.getLastInteraction().getPlayer().getUniqueId()));
            interactionMap.put(ops.createString("timestamp"), Codecs.LONG.encode(ops, value.getLastInteraction().getTimestamp()));
            map.put(ops.createString("last_interaction"), ops.createMap(interactionMap));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Interaction interaction)) return;

        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("interaction_width")))).onSuccess(interaction::setInteractionWidth);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("interaction_height")))).onSuccess(interaction::setInteractionHeight);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("responsive")))).onSuccess(interaction::setResponsive);
    }
}