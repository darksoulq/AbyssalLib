package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import org.bukkit.inventory.ItemStack;

public class OminousAmplifier extends DataComponent<OminousBottleAmplifier> implements Vanilla {
    private static final Codec<DataComponent<OminousBottleAmplifier>> CODEC = Codec.of(null, null);

    public OminousAmplifier(OminousBottleAmplifier amplifier) {
        super(Identifier.of(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER.key().asString()), amplifier, CODEC);
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
