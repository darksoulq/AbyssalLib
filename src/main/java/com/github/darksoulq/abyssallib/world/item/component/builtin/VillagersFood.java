package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.VillagerFood;
import org.bukkit.inventory.ItemStack;

public class VillagersFood extends DataComponent<VillagerFood> implements Vanilla {
    public static final Codec<VillagersFood> CODEC = Codecs.INT.xmap(
        value -> new VillagersFood(VillagerFood.villagerFood(value)),
        food -> food.getValue().nutrition()
    );

    public static final DataComponentType<VillagersFood> TYPE = DataComponentType.valued(CODEC, VillagersFood::new);

    public VillagersFood(VillagerFood food) {
        super(food);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.VILLAGER_FOOD, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.VILLAGER_FOOD);
    }
}
