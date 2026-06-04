package com.github.darksoulq.abyssallib.world.data.statistic;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import net.kyori.adventure.key.Key;

import java.util.Objects;

/**
 * Represents a unique trackable statistic associated with a player.
 *
 * @param type   The broad categorization type of the statistic.
 * @param target The specific target identifier within that category.
 */
public record Statistic(StatisticType type, Key target) {

    /**
     * A structured codec for encoding and decoding a Statistic to and from a string format.
     * The serialized format is expected to be "type_id/target_id".
     */
    public static final Codec<Statistic> CODEC = Codecs.STRING.comapFlatMap(
        input -> {
            int separator = input.indexOf('/');
            if (separator == -1) {
                return DataResult.error("Invalid statistic format: " + input);
            }

            String typeId = input.substring(0, separator);
            String targetId = input.substring(separator + 1);

            StatisticType type = Registries.STATISTIC_TYPES.get(typeId);
            if (type == null) {
                return DataResult.error("Unknown statistic type: " + typeId);
            }

            try {
                return DataResult.success(new Statistic(type, Key.key(targetId)));
            } catch (Exception e) {
                return DataResult.error("Invalid target key format: " + targetId);
            }
        },
        stat -> stat.type().id().asString() + "/" + stat.target().asString()
    ).describe("Statistic");

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistic(StatisticType thatType, Key thatTarget))) return false;
        return Objects.equals(this.type, thatType) && Objects.equals(this.target, thatTarget);
    }
}