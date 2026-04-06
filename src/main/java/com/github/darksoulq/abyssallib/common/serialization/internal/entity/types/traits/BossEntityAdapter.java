package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
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
    public <D> void serialize(DynamicOps<D> ops, Boss value, Map<D, D> map) throws Codec.CodecException {
        if (value.getBossBar() != null) {
            map.put(ops.createString("boss_bar_title"), Codecs.STRING.encode(ops, value.getBossBar().getTitle()));
            map.put(ops.createString("boss_bar_color"), Codecs.STRING.encode(ops, value.getBossBar().getColor().name()));
            map.put(ops.createString("boss_bar_style"), Codecs.STRING.encode(ops, value.getBossBar().getStyle().name()));
            map.put(ops.createString("boss_bar_progress"), Codecs.DOUBLE.encode(ops, value.getBossBar().getProgress()));
            map.put(ops.createString("boss_bar_visible"), Codecs.BOOLEAN.encode(ops, value.getBossBar().isVisible()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Boss boss)) return;
        if (boss.getBossBar() == null) return;

        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("boss_bar_title")))).onSuccess(boss.getBossBar()::setTitle);
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("boss_bar_color")))).onSuccess(s -> boss.getBossBar().setColor(BarColor.valueOf(s)));
        Try.of(() -> Codecs.STRING.decode(ops, map.get(ops.createString("boss_bar_style")))).onSuccess(s -> boss.getBossBar().setStyle(BarStyle.valueOf(s)));
        Try.of(() -> Codecs.DOUBLE.decode(ops, map.get(ops.createString("boss_bar_progress")))).onSuccess(boss.getBossBar()::setProgress);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("boss_bar_visible")))).onSuccess(boss.getBossBar()::setVisible);
    }
}