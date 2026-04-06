package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import java.util.Map;

public class LightningStrikeEntityAdapter extends EntityAdapter<LightningStrike> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof LightningStrike;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, LightningStrike value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("flash_count"), Codecs.INT.encode(ops, value.getFlashCount()));
        map.put(ops.createString("life_ticks"), Codecs.INT.encode(ops, value.getLifeTicks()));

        if (value.getCausingPlayer() != null) {
            map.put(ops.createString("causing_player_uuid"), Codecs.UUID.encode(ops, value.getCausingPlayer().getUniqueId()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof LightningStrike lightning)) return;

        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("flash_count")))).onSuccess(lightning::setFlashCount);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("life_ticks")))).onSuccess(lightning::setLifeTicks);

        D playerData = map.get(ops.createString("causing_player_uuid"));
        if (playerData != null) {
            Try.of(() -> Codecs.UUID.decode(ops, playerData)).onSuccess(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    lightning.setCausingPlayer(player);
                }
            });
        }
    }
}