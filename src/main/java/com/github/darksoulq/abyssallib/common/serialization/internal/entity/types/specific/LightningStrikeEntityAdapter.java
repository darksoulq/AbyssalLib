package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
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
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, LightningStrike value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("flash_count", Codecs.INT, value.getFlashCount())
            .write("life_ticks", Codecs.INT, value.getLifeTicks());

        if (value.getCausingPlayer() != null) {
            ctx.write("causing_player_uuid", Codecs.UUID, value.getCausingPlayer().getUniqueId());
        }

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof LightningStrike lightning)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("flash_count", Codecs.INT, opt -> opt.ifPresent(lightning::setFlashCount))
            .readOptional("life_ticks", Codecs.INT, opt -> opt.ifPresent(lightning::setLifeTicks))
            .readOptional("causing_player_uuid", Codecs.UUID, opt -> opt.ifPresent(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    lightning.setCausingPlayer(player);
                }
            }));

        return ctx.result();
    }
}