package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;

@SuppressWarnings("UnstableApiUsage")
public class Trim extends DataComponent<ArmorTrim> implements Vanilla {
    private static final Codec<Trim> CODEC = ExtraCodecs.ARMOR_TRIM.xmap(
            Trim::new,
            Trim::getValue
    );

    public Trim(ItemArmorTrim trim) {
        super(Identifier.of(DataComponentTypes.TRIM.key().asString()), trim.armorTrim(), CODEC);
    }
    public Trim(ArmorTrim trim) {
        super(Identifier.of(DataComponentTypes.TRIM.key().asString()), trim, CODEC);
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(value).build());
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.TRIM);
    }
}
