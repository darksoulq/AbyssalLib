package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class BaseColor extends DataComponent<DyeColor> implements Vanilla {
    public static final Codec<BaseColor> CODEC = Codec.enumCodec(DyeColor.class).xmap(
        BaseColor::new,
        BaseColor::getValue
    );
    public static final DataComponentType<BaseColor> TYPE = DataComponentType.valued(CODEC, BaseColor::new);

    public BaseColor(DyeColor color) {
        super(color);
    }

    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.BASE_COLOR, value);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.BASE_COLOR);
    }
}