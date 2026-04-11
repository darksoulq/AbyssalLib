package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;

import java.util.Objects;

public record Statistic(StatisticType type, Key target) {

    public static final Codec<Statistic> CODEC = Codecs.STRING.xmap(
        input -> {
            int separator = input.indexOf('/');
            if (separator == -1) {
                throw new Codec.CodecException("Invalid statistic format: " + input);
            }

            String typeId = input.substring(0, separator);
            String targetId = input.substring(separator + 1);

            StatisticType type = Registries.STATISTIC_TYPES.get(typeId);
            if (type == null) {
                throw new Codec.CodecException("Unknown statistic type: " + typeId);
            }

            return new Statistic(type, Key.key(targetId));
        },
        stat -> stat.type().id().asString() + "/" + stat.target().asString()
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistic(StatisticType type, Key target))) return false;
        return Objects.equals(this.type, type) && Objects.equals(this.target, target);
    }

}