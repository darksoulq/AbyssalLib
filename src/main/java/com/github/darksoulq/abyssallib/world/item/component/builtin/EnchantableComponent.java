package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class EnchantableComponent extends DataComponent<Integer> implements Vanilla {
    private static final Codec<EnchantableComponent> CODEC = Codecs.INT.xmap(
            EnchantableComponent::new,
            EnchantableComponent::getValue
    );

    public EnchantableComponent(Enchantable level) {
        super(Identifier.of(DataComponentTypes.ENCHANTABLE.key().asString()), level.value(), CODEC);
    }
    public EnchantableComponent(int level) {
        super(Identifier.of(DataComponentTypes.ENCHANTABLE.key().asString()), level, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(value));
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ENCHANTABLE);
    }
}
