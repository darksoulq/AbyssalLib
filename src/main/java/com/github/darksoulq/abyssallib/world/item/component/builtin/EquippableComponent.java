package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.inventory.ItemStack;

public class EquippableComponent extends DataComponent<Equippable> implements Vanilla {
    private static final Codec<DataComponent<Equippable>> CODEC = Codec.of(null, null);

    public EquippableComponent(Equippable equip) {
        super(Identifier.of(DataComponentTypes.EQUIPPABLE.key().asString()), equip, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.EQUIPPABLE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.EQUIPPABLE);
    }
}
