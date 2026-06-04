package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.specific;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import java.util.Map;

public class TextDisplayEntityAdapter extends EntityAdapter<TextDisplay> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof TextDisplay;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, TextDisplay value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("text_component", Codecs.TEXT_COMPONENT, value.text())
            .write("line_width", Codecs.INT, value.getLineWidth())
            .write("text_opacity", Codecs.BYTE, value.getTextOpacity())
            .write("is_shadowed", Codecs.BOOLEAN, value.isShadowed())
            .write("is_see_through", Codecs.BOOLEAN, value.isSeeThrough())
            .write("is_default_background", Codecs.BOOLEAN, value.isDefaultBackground())
            .write("alignment", Codecs.TEXT_ALIGNMENT, value.getAlignment())
            .writeNullable("background_color", Codecs.COLOR, value.getBackgroundColor());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof TextDisplay display)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("text_component", Codecs.TEXT_COMPONENT, opt -> opt.ifPresent(display::text))
            .readOptional("line_width", Codecs.INT, opt -> opt.ifPresent(display::setLineWidth))
            .readOptional("text_opacity", Codecs.BYTE, opt -> opt.ifPresent(display::setTextOpacity))
            .readOptional("is_shadowed", Codecs.BOOLEAN, opt -> opt.ifPresent(display::setShadowed))
            .readOptional("is_see_through", Codecs.BOOLEAN, opt -> opt.ifPresent(display::setSeeThrough))
            .readOptional("is_default_background", Codecs.BOOLEAN, opt -> opt.ifPresent(display::setDefaultBackground))
            .readOptional("alignment", Codecs.TEXT_ALIGNMENT, opt -> opt.ifPresent(display::setAlignment))
            .readOptional("background_color", Codecs.COLOR, opt -> opt.ifPresent(display::setBackgroundColor));

        return ctx.result();
    }
}