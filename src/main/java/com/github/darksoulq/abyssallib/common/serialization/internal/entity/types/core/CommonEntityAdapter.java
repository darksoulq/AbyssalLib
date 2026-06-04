package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.core;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Map;

public class CommonEntityAdapter extends EntityAdapter<Entity> {

    @Override
    public boolean doesApply(Entity entity) {
        return true;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Entity value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.writeNullable("custom_name", Codecs.TEXT_COMPONENT, value.customName())
            .write("custom_name_visible", Codecs.BOOLEAN, value.isCustomNameVisible())
            .write("glowing", Codecs.BOOLEAN, value.isGlowing())
            .write("gravity", Codecs.BOOLEAN, value.hasGravity())
            .write("invulnerable", Codecs.BOOLEAN, value.isInvulnerable())
            .write("silent", Codecs.BOOLEAN, value.isSilent())
            .write("fire_ticks", Codecs.INT, value.getFireTicks())
            .write("ticks_lived", Codecs.INT, value.getTicksLived())
            .write("scoreboard_tags", Codecs.STRING.list(), new ArrayList<>(value.getScoreboardTags()));

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readNullable("custom_name", Codecs.TEXT_COMPONENT, base::customName)
            .readOptional("custom_name_visible", Codecs.BOOLEAN, opt -> opt.ifPresent(base::setCustomNameVisible))
            .readOptional("glowing", Codecs.BOOLEAN, opt -> opt.ifPresent(base::setGlowing))
            .readOptional("gravity", Codecs.BOOLEAN, opt -> opt.ifPresent(base::setGravity))
            .readOptional("invulnerable", Codecs.BOOLEAN, opt -> opt.ifPresent(base::setInvulnerable))
            .readOptional("silent", Codecs.BOOLEAN, opt -> opt.ifPresent(base::setSilent))
            .readOptional("fire_ticks", Codecs.INT, opt -> opt.ifPresent(base::setFireTicks))
            .readOptional("ticks_lived", Codecs.INT, opt -> opt.ifPresent(base::setTicksLived))
            .readOptional("scoreboard_tags", Codecs.STRING.list(), opt -> opt.ifPresent(tags -> {
                base.getScoreboardTags().clear();
                base.getScoreboardTags().addAll(tags);
            }));

        return ctx.result();
    }
}