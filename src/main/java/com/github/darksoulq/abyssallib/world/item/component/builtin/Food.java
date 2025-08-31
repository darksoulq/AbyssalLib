package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.inventory.ItemStack;

public class Food extends DataComponent<FoodProperties> implements Vanilla {
    private static final Codec<DataComponent<FoodProperties>> CODEC = Codec.of(null, null);

    public Food(FoodProperties props) {
        super(Identifier.of(DataComponentTypes.FOOD.key().asString()), props, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.FOOD, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.FOOD);
    }
}
