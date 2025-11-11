package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.inventory.ItemStack;

public class ItemAttributeModifier extends DataComponent<ItemAttributeModifiers> implements Vanilla {
    private static final Codec<ItemAttributeModifier> CODEC = ExtraCodecs.ITEM_ATTRIBUTE_MODIFIERS.xmap(
            ItemAttributeModifier::new,
            ItemAttributeModifier::getValue
    );

    public ItemAttributeModifier(ItemAttributeModifiers modifiers) {
        super(Identifier.of(DataComponentTypes.ATTRIBUTE_MODIFIERS.key().asString()), modifiers, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    }
}
