package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.IntRange;

public class OminousAmplifier extends DataComponent<OminousBottleAmplifier> implements Vanilla {
    private static final Codec<DataComponent<OminousBottleAmplifier>> CODEC = Codecs.INT.xmap(
            OminousAmplifier::new,
            o -> o.value.amplifier()
    );

    @IntRange(from = 0, to = 4)
    public OminousAmplifier(int amplifier) {
        super(Identifier.of(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER.key().asString()), OminousBottleAmplifier.amplifier(amplifier), CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
    }
}
