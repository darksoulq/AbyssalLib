package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.inventory.ItemStack;

public class ItemAttributeModifier extends DataComponent<ItemAttributeModifiers> implements Vanilla {
    public static final Codec<ItemAttributeModifier> CODEC = ExtraCodecs.ITEM_ATTRIBUTE_MODIFIERS.xmap(
            ItemAttributeModifier::new,
            ItemAttributeModifier::getValue
    );
    public static final DataComponentType<ItemAttributeModifier> TYPE = DataComponentType.valued(CODEC, ItemAttributeModifier::new);

    public ItemAttributeModifier(ItemAttributeModifiers modifiers) {
        super(modifiers);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
