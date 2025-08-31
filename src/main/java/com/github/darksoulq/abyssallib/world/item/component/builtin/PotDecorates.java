package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import org.bukkit.inventory.ItemStack;

public class PotDecorates extends DataComponent<PotDecorations> implements Vanilla {
    private static final Codec<DataComponent<PotDecorations>> CODEC = Codec.of(null, null);

    public PotDecorates(PotDecorations decor) {
        super(Identifier.of(DataComponentTypes.POT_DECORATIONS.key().asString()), decor, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.POT_DECORATIONS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.POT_DECORATIONS);
    }
}
