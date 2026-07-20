package com.github.darksoulq.abyssallib.world.advancement.criterion;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.attribute.Attribute;
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

/**
 * An advancement criterion evaluating custom RPG attributes of a player.
 */
public class CustomAttributeCriterion implements AdvancementCriterion {

    /**
     * The codec used for serializing and deserializing the custom attribute criterion.
     */
    public static final Codec<CustomAttributeCriterion> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("attribute").forGetter(CustomAttributeCriterion.class, p -> p.attrKey),
        Codecs.DOUBLE.fieldOf("threshold").forGetter(CustomAttributeCriterion.class, p -> p.threshold)
    ).apply(instance, CustomAttributeCriterion::new)).describe("CustomAttributeCriterion");

    /**
     * The registered type definition for the custom attribute criterion.
     */
    public static final CriterionType<CustomAttributeCriterion> TYPE = () -> CODEC;

    private final Key attrKey;
    private final double threshold;

    /**
     * Constructs a new CustomAttributeCriterion.
     *
     * @param attrKey   The key of the custom attribute.
     * @param threshold The required value.
     */
    public CustomAttributeCriterion(Key attrKey, double threshold) {
        this.attrKey = attrKey;
        this.threshold = threshold;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    /**
     * Checks if the player's custom attribute value meets or exceeds the threshold.
     *
     * @param player The player to evaluate.
     * @return True if the condition is met.
     */
    @Override
    public boolean isMet(Player player) {
        Attribute attribute = Registries.ATTRIBUTES.get(attrKey.asString());
        if (attribute == null) {
            return false;
        }

        EntityAttributes attributes = EntityAttributes.of(player);
        return attributes.getValue(attribute) >= threshold;
    }

    
}