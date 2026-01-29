package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class Food extends DataComponent<FoodProperties> implements Vanilla {
    public static final Codec<Food> CODEC = ExtraCodecs.FOOD_PROPERTIES.xmap(
            Food::new,
            Food::getValue
    );
    public static final DataComponentType<Food> TYPE = DataComponentType.valued(CODEC, Food::new);

    public Food(FoodProperties props) {
        super(props);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
