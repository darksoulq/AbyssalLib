package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import java.util.Map;

public class TextDisplayEntityAdapter extends EntityAdapter<TextDisplay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof TextDisplay;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, TextDisplay value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("text_component"), Codecs.TEXT_COMPONENT.encode(ops, value.text()));
        map.put(ops.createString("line_width"), Codecs.INT.encode(ops, value.getLineWidth()));
        map.put(ops.createString("text_opacity"), Codecs.BYTE.encode(ops, value.getTextOpacity()));
        map.put(ops.createString("is_shadowed"), Codecs.BOOLEAN.encode(ops, value.isShadowed()));
        map.put(ops.createString("is_see_through"), Codecs.BOOLEAN.encode(ops, value.isSeeThrough()));
        map.put(ops.createString("is_default_background"), Codecs.BOOLEAN.encode(ops, value.isDefaultBackground()));
        map.put(ops.createString("alignment"), Codecs.TEXT_ALIGNMENT.encode(ops, value.getAlignment()));

        if (value.getBackgroundColor() != null) {
            map.put(ops.createString("background_color"), Codecs.COLOR.encode(ops, value.getBackgroundColor()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof TextDisplay display)) return;

        Try.of(() -> Codecs.TEXT_COMPONENT.decode(ops, map.get(ops.createString("text_component")))).onSuccess(display::text);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("line_width")))).onSuccess(display::setLineWidth);
        Try.of(() -> Codecs.BYTE.decode(ops, map.get(ops.createString("text_opacity")))).onSuccess(display::setTextOpacity);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_shadowed")))).onSuccess(display::setShadowed);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_see_through")))).onSuccess(display::setSeeThrough);
        Try.of(() -> Codecs.BOOLEAN.decode(ops, map.get(ops.createString("is_default_background")))).onSuccess(display::setDefaultBackground);
        Try.of(() -> Codecs.TEXT_ALIGNMENT.decode(ops, map.get(ops.createString("alignment")))).onSuccess(display::setAlignment);

        D bgColorData = map.get(ops.createString("background_color"));
        if (bgColorData != null) {
            Try.of(() -> Codecs.COLOR.decode(ops, bgColorData)).onSuccess(display::setBackgroundColor);
        }
    }
}