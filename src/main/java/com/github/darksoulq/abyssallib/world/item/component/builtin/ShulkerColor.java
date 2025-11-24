package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class ShulkerColor extends DataComponent<DyeColor> implements Vanilla {
    private static final Codec<ShulkerColor> CODEC = Codec.enumCodec(DyeColor.class).xmap(
            ShulkerColor::new,
            ShulkerColor::getValue
    );

    public ShulkerColor(DyeColor color) {
        super(Identifier.of(DataComponentTypes.SHULKER_COLOR.key().asString()), color, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.SHULKER_COLOR, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.SHULKER_COLOR);
    }
}
