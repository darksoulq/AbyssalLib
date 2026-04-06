package com.github.darksoulq.abyssallib.common.serialization.internal.entity.types.traits;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.entity.EntityAdapter;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;

import java.util.Map;

public class DisplayEntityAdapter extends EntityAdapter<Display> {

    @Override
    public boolean doesApply(Entity entity) {
        return entity instanceof Display;
    }

    @Override
    public <D> void serialize(DynamicOps<D> ops, Display value, Map<D, D> map) throws Codec.CodecException {
        map.put(ops.createString("transformation"), Codecs.TRANSFORMATION.encode(ops, value.getTransformation()));
        map.put(ops.createString("interpolation_duration"), Codecs.INT.encode(ops, value.getInterpolationDuration()));
        map.put(ops.createString("teleport_duration"), Codecs.INT.encode(ops, value.getTeleportDuration()));
        map.put(ops.createString("view_range"), Codecs.FLOAT.encode(ops, value.getViewRange()));
        map.put(ops.createString("shadow_radius"), Codecs.FLOAT.encode(ops, value.getShadowRadius()));
        map.put(ops.createString("shadow_strength"), Codecs.FLOAT.encode(ops, value.getShadowStrength()));
        map.put(ops.createString("display_width"), Codecs.FLOAT.encode(ops, value.getDisplayWidth()));
        map.put(ops.createString("display_height"), Codecs.FLOAT.encode(ops, value.getDisplayHeight()));
        map.put(ops.createString("interpolation_delay"), Codecs.INT.encode(ops, value.getInterpolationDelay()));
        map.put(ops.createString("billboard"), Codecs.BILLBOARD.encode(ops, value.getBillboard()));

        if (value.getGlowColorOverride() != null) {
            map.put(ops.createString("glow_color_override"), Codecs.COLOR.encode(ops, value.getGlowColorOverride()));
        }

        if (value.getBrightness() != null) {
            map.put(ops.createString("brightness"), Codecs.DISPLAY_BRIGHTNESS.encode(ops, value.getBrightness()));
        }
    }

    @Override
    public <D> void deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base) throws Codec.CodecException {
        if (!(base instanceof Display display)) return;

        Try.of(() -> Codecs.TRANSFORMATION.decode(ops, map.get(ops.createString("transformation")))).onSuccess(display::setTransformation);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("interpolation_duration")))).onSuccess(display::setInterpolationDuration);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("teleport_duration")))).onSuccess(display::setTeleportDuration);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("view_range")))).onSuccess(display::setViewRange);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("shadow_radius")))).onSuccess(display::setShadowRadius);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("shadow_strength")))).onSuccess(display::setShadowStrength);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("display_width")))).onSuccess(display::setDisplayWidth);
        Try.of(() -> Codecs.FLOAT.decode(ops, map.get(ops.createString("display_height")))).onSuccess(display::setDisplayHeight);
        Try.of(() -> Codecs.INT.decode(ops, map.get(ops.createString("interpolation_delay")))).onSuccess(display::setInterpolationDelay);
        Try.of(() -> Codecs.BILLBOARD.decode(ops, map.get(ops.createString("billboard")))).onSuccess(display::setBillboard);

        D glowData = map.get(ops.createString("glow_color_override"));
        if (glowData != null) {
            Try.of(() -> Codecs.COLOR.decode(ops, glowData)).onSuccess(display::setGlowColorOverride);
        }

        D brightnessData = map.get(ops.createString("brightness"));
        if (brightnessData != null) {
            Try.of(() -> Codecs.DISPLAY_BRIGHTNESS.decode(ops, brightnessData)).onSuccess(display::setBrightness);
        }
    }
}