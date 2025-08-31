package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import org.bukkit.inventory.ItemStack;

public class Trim extends DataComponent<ItemArmorTrim> implements Vanilla {
    private static final Codec<DataComponent<ItemArmorTrim>> CODEC = Codec.of(null, null);

    public Trim(ItemArmorTrim trim) {
        super(Identifier.of(DataComponentTypes.TRIM.key().asString()), trim, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.TRIM, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.TRIM);
    }
}
