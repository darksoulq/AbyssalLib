package com.github.darksoulq.abyssallib.server.cooldown;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.scheduler.TimeUnit;
import net.kyori.adventure.key.Key;

/**
 * Defines a structured cooldown configuration, representing its identifier,
 * standard duration, unit of measurement, and behavioral enforcement policy.
 *
 * @param id              The unique namespace identifier for the cooldown.
 * @param defaultDuration The standard length of the cooldown.
 * @param defaultUnit     The time unit scaling the duration.
 * @param defaultPolicy   The policy defining how the cooldown behaves across sessions or resets.
 */
public record CooldownType(Key id, long defaultDuration, TimeUnit defaultUnit, CooldownPolicy defaultPolicy) {

    /**
     * The codec responsible for serializing and deserializing cooldown types securely natively.
     */
    public static final Codec<CooldownType> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("id").forGetter(CooldownType.class, CooldownType::id),
        Codecs.LONG.fieldOf("default_duration").forGetter(CooldownType.class, CooldownType::defaultDuration),
        Codec.enumCodec(TimeUnit.class).fieldOf("default_unit").forGetter(CooldownType.class, CooldownType::defaultUnit),
        Codec.enumCodec(CooldownPolicy.class).fieldOf("default_policy").forGetter(CooldownType.class, CooldownType::defaultPolicy)
    ).apply(instance, CooldownType::new)).describe("CooldownType");

    /**
     * Constructs a standard cooldown configuration utilizing the default keeping policy.
     *
     * @param id              The unique namespace identifier.
     * @param defaultDuration The length of the cooldown.
     * @param defaultUnit     The associated time unit.
     */
    public CooldownType(Key id, long defaultDuration, TimeUnit defaultUnit) {
        this(id, defaultDuration, defaultUnit, CooldownPolicy.KEEP);
    }
}