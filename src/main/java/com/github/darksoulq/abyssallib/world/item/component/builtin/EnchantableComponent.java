package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class EnchantableComponent extends DataComponent<Integer> implements Vanilla {
    public static final Codec<EnchantableComponent> CODEC = Codecs.INT.xmap(
            EnchantableComponent::new,
            EnchantableComponent::getValue
    );
    public static final DataComponentType<EnchantableComponent> TYPE = DataComponentType.valued(CODEC, EnchantableComponent::new);

    public EnchantableComponent(int level) {
        super(level);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
