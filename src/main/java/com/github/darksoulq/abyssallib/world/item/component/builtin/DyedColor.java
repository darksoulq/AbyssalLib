package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

public class DyedColor extends DataComponent<Color> implements Vanilla {
    private static final Codec<DyedColor> CODEC = ExtraCodecs.COLOR.xmap(
            DyedColor::new,
            DyedColor::getValue
    );

    public DyedColor(DyedItemColor color) {
        super(Identifier.of(DataComponentTypes.DYED_COLOR.key().asString()), color.color(), CODEC);
    }
    public DyedColor(Color color) {
        super(Identifier.of(DataComponentTypes.DYED_COLOR.key().asString()), color, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DYED_COLOR);
    }
}
