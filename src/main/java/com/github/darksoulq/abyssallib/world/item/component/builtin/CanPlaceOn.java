package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import org.bukkit.inventory.ItemStack;

public class CanPlaceOn extends DataComponent<ItemAdventurePredicate> implements Vanilla {
    private static final Codec<DataComponent<ItemAdventurePredicate>> CODEC = Codec.of(null, null);

    public CanPlaceOn(ItemAdventurePredicate blocks) {
        super(Identifier.of(DataComponentTypes.CAN_PLACE_ON.key().asString()), blocks, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.CAN_PLACE_ON, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.CAN_PLACE_ON);
    }
}
