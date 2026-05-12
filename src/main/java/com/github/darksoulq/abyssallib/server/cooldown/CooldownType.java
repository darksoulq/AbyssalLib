package com.github.darksoulq.abyssallib.server.cooldown;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordCodecBuilder;
import com.github.darksoulq.abyssallib.server.scheduler.TimeUnit;
import net.kyori.adventure.key.Key;

public record CooldownType(Key id, long defaultDuration, TimeUnit defaultUnit, CooldownPolicy defaultPolicy) {

    public static final Codec<CooldownType> CODEC = RecordCodecBuilder.create(
            Codecs.KEY.fieldOf("id", CooldownType::id),
            Codecs.LONG.fieldOf("default_duration", CooldownType::defaultDuration),
            Codec.enumCodec(TimeUnit.class).fieldOf("default_unit", CooldownType::defaultUnit),
            Codec.enumCodec(CooldownPolicy.class).fieldOf("default_policy", CooldownType::defaultPolicy),
            CooldownType::new
    );

    public CooldownType(Key id, long defaultDuration, TimeUnit defaultUnit) {
        this(id, defaultDuration, defaultUnit, CooldownPolicy.KEEP);
    }
}