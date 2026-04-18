package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class Dye extends DataComponent<DyeColor> implements Vanilla {
    public static final Codec<Dye> CODEC = Codec.enumCodec(DyeColor.class).xmap(
        Dye::new,
        Dye::getValue
    );

    public static final DataComponentType<Dye> TYPE = DataComponentType.valued(CODEC, v -> new Dye((DyeColor) v));


    public Dye(DyeColor color) {
        super(color);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.DYE, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.DYE);
    }
}
