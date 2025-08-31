package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

public class PotionDurationScale extends DataComponent<Float> implements Vanilla {
    private static final Codec<DataComponent<Float>> CODEC = Codec.of(null, null);

    public PotionDurationScale(float value) {
        super(Identifier.of(DataComponentTypes.POTION_DURATION_SCALE.key().asString()), value, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.POTION_DURATION_SCALE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.POTION_DURATION_SCALE);
    }
}
