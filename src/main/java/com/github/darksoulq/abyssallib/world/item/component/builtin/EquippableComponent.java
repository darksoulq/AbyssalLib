package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.inventory.ItemStack;

public class EquippableComponent extends DataComponent<Equippable> implements Vanilla {
    public static final Codec<EquippableComponent> CODEC = ExtraCodecs.EQUIPPABLE.xmap(
            EquippableComponent::new,
            EquippableComponent::getValue
    );
    public static final DataComponentType<EquippableComponent> TYPE = DataComponentType.valued(CODEC, EquippableComponent::new);

    public EquippableComponent(Equippable equip) {
        super(equip);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
