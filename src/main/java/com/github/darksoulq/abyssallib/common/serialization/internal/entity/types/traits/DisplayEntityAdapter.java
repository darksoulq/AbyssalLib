package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;

import java.util.Map;

public class DisplayEntityAdapter extends EntityAdapter<Display> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Display;
    }

    @Override
    public <D> DataResult<Void> serialize(DynamicOps<D> ops, Display value, Map<D, D> map) {
        EncodeContext<D> ctx = EncodeContext.of(ops, map);

        ctx.write("transformation", Codecs.TRANSFORMATION, value.getTransformation())
            .write("interpolation_duration", Codecs.INT, value.getInterpolationDuration())
            .write("teleport_duration", Codecs.INT, value.getTeleportDuration())
            .write("view_range", Codecs.FLOAT, value.getViewRange())
            .write("shadow_radius", Codecs.FLOAT, value.getShadowRadius())
            .write("shadow_strength", Codecs.FLOAT, value.getShadowStrength())
            .write("display_width", Codecs.FLOAT, value.getDisplayWidth())
            .write("display_height", Codecs.FLOAT, value.getDisplayHeight())
            .write("interpolation_delay", Codecs.INT, value.getInterpolationDelay())
            .write("billboard", Codecs.BILLBOARD, value.getBillboard())
            .writeNullable("glow_color_override", Codecs.COLOR, value.getGlowColorOverride())
            .writeNullable("brightness", Codecs.DISPLAY_BRIGHTNESS, value.getBrightness());

        DataResult<D> result = ctx.result();
        return result.isSuccess() ? DataResult.success(null) : DataResult.partial(null, result.warnings());
    }

    @Override
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) {
        if (!(base instanceof Display display)) return DataResult.success(null);
        DecodeContext<D> ctx = DecodeContext.of(ops, map);

        ctx.readOptional("transformation", Codecs.TRANSFORMATION, opt -> opt.ifPresent(display::setTransformation))
            .readOptional("interpolation_duration", Codecs.INT, opt -> opt.ifPresent(display::setInterpolationDuration))
            .readOptional("teleport_duration", Codecs.INT, opt -> opt.ifPresent(display::setTeleportDuration))
            .readOptional("view_range", Codecs.FLOAT, opt -> opt.ifPresent(display::setViewRange))
            .readOptional("shadow_radius", Codecs.FLOAT, opt -> opt.ifPresent(display::setShadowRadius))
            .readOptional("shadow_strength", Codecs.FLOAT, opt -> opt.ifPresent(display::setShadowStrength))
            .readOptional("display_width", Codecs.FLOAT, opt -> opt.ifPresent(display::setDisplayWidth))
            .readOptional("display_height", Codecs.FLOAT, opt -> opt.ifPresent(display::setDisplayHeight))
            .readOptional("interpolation_delay", Codecs.INT, opt -> opt.ifPresent(display::setInterpolationDelay))
            .readOptional("billboard", Codecs.BILLBOARD, opt -> opt.ifPresent(display::setBillboard))
            .readOptional("glow_color_override", Codecs.COLOR, opt -> opt.ifPresent(display::setGlowColorOverride))
            .readOptional("brightness", Codecs.DISPLAY_BRIGHTNESS, opt -> opt.ifPresent(display::setBrightness));

        return ctx.result();
    }
}