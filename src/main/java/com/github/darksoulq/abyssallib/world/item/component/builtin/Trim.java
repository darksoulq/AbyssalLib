package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;

@SuppressWarnings("UnstableApiUsage")
public class Trim extends DataComponent<ArmorTrim> implements Vanilla {
    public static final Codec<Trim> CODEC = ExtraCodecs.ARMOR_TRIM.xmap(
            Trim::new,
            Trim::getValue
    );
    public static final DataComponentType<Trim> TYPE = DataComponentType.valued(CODEC, v -> new Trim((ItemArmorTrim) v));

    public Trim(ItemArmorTrim trim) {
        super(trim.armorTrim());
    }
    public Trim(ArmorTrim trim) {
        super(trim);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
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
