package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import org.bukkit.inventory.ItemStack;

public class EnchantableComponent extends DataComponent<Enchantable> implements Vanilla {
    private static final Codec<DataComponent<Enchantable>> CODEC = Codec.of(null, null);

    public EnchantableComponent(Enchantable enchantable) {
        super(Identifier.of(DataComponentTypes.ENCHANTABLE.key().asString()), enchantable, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ENCHANTABLE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ENCHANTABLE);
    }
}
