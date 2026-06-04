package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;

import java.util.Map;

public class BossEntityAdapter extends EntityAdapter<Boss> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Boss;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Boss value, Map<D, D> map) {
        if (value.getBossBar() == null) return DataResult.success(null);
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("boss_bar_title", Codecs.STRING, value.getBossBar().getTitle())
            .write("boss_bar_color", Codecs.STRING, value.getBossBar().getColor().name())
            .write("boss_bar_style", Codecs.STRING, value.getBossBar().getStyle().name())
            .write("boss_bar_progress", Codecs.DOUBLE, value.getBossBar().getProgress())
            .write("boss_bar_visible", Codecs.BOOLEAN, value.getBossBar().isVisible());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Boss boss) || boss.getBossBar() == null) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("boss_bar_title", Codecs.STRING, opt -> opt.ifPresent(boss.getBossBar()::setTitle))
            .readOptional("boss_bar_color", Codecs.STRING, opt -> opt.ifPresent(color -> {
                try {
                    boss.getBossBar().setColor(BarColor.valueOf(color));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("boss_bar_style", Codecs.STRING, opt -> opt.ifPresent(style -> {
                try {
                    boss.getBossBar().setStyle(BarStyle.valueOf(style));
                } catch (Exception ignored) {
                }
            }))
            .readOptional("boss_bar_progress", Codecs.DOUBLE, opt -> opt.ifPresent(boss.getBossBar()::setProgress))
            .readOptional("boss_bar_visible", Codecs.BOOLEAN, opt -> opt.ifPresent(boss.getBossBar()::setVisible));

        return ctx.result();
    }
}