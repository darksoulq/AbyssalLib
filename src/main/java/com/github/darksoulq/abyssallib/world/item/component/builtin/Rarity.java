package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

public class Rarity extends DataComponent<ItemRarity> implements Vanilla {
    public static final Codec<Rarity> CODEC = Codec.enumCodec(ItemRarity.class).xmap(
            Rarity::new,
            Rarity::getValue
    );
    public static final DataComponentType<Rarity> TYPE = DataComponentType.valued(CODEC, Rarity::new);

    public Rarity(ItemRarity rarity) {
        super(rarity);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.RARITY, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.RARITY);
    }
}
